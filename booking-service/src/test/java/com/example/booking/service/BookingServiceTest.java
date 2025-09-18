package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.entity.ShowSeat;
import com.example.booking.repository.ShowSeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RLock;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    ShowSeatRepository seatRepository = Mockito.mock(ShowSeatRepository.class);
    RedisLockService lockService = Mockito.mock(RedisLockService.class);
    KafkaTemplate<String,String> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

    BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingService = new BookingService(seatRepository, lockService, kafkaTemplate);
    }

    @Test
    void successfulBooking() throws InterruptedException {
        BookingRequest req = new BookingRequest();
        req.setShowId(UUID.randomUUID());
        req.setSeatNumbers(List.of("A1","A2"));
        ShowSeat s1 = new ShowSeat(UUID.randomUUID(), req.getShowId(), "A1", ShowSeat.Status.AVAILABLE, 0L);
        ShowSeat s2 = new ShowSeat(UUID.randomUUID(), req.getShowId(), "A2", ShowSeat.Status.AVAILABLE, 0L);

        when(lockService.acquireMultiLock(any(), anyLong(), anyLong())).thenReturn(Mockito.mock(RLock.class));
        when(seatRepository.findByShowIdAndSeatNumberIn(eq(req.getShowId()), any())).thenReturn(List.of(s1,s2));
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        BookingResponse resp = bookingService.bookSeats(req);
        assertThat(resp.isSuccess()).isTrue();
        verify(seatRepository, times(1)).saveAll(any());
    }

    @Test
    void seatUnavailable() throws InterruptedException {
        BookingRequest req = new BookingRequest();
        req.setShowId(UUID.randomUUID());
        req.setSeatNumbers(List.of("A1"));
        ShowSeat s1 = new ShowSeat(UUID.randomUUID(), req.getShowId(), "A1", ShowSeat.Status.BOOKED, 0L);

        when(lockService.acquireMultiLock(any(), anyLong(), anyLong())).thenReturn(Mockito.mock(RLock.class));
        when(seatRepository.findByShowIdAndSeatNumberIn(eq(req.getShowId()), any())).thenReturn(List.of(s1));

        BookingResponse resp = bookingService.bookSeats(req);
        assertThat(resp.isSuccess()).isFalse();
        assertThat(resp.getMessage()).contains("not available");
    }
