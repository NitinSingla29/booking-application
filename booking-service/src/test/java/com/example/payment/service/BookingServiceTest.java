package com.example.payment.service;


import com.example.core.enumeration.OperationStatus;
import com.example.core.enumeration.PaymentStatus;
import com.example.core.enumeration.SeatReleaseStatus;
import com.example.payment.BaseTest;
import com.example.payment.BookingStatus;
import com.example.payment.client.InventoryClient;
import com.example.payment.client.PaymentClient;
import com.example.payment.client.PricingClient;
import com.example.payment.client.transfer.inventory.SeatConfirmResponse;
import com.example.payment.client.transfer.inventory.SeatHoldResponse;
import com.example.payment.client.transfer.inventory.SeatReleaseResponse;
import com.example.payment.client.transfer.pricing.ShowPriceCalculationResponse;
import com.example.payment.domain.jpa.Booking;
import com.example.payment.repository.jpa.IBookingRepository;
import com.example.payment.transfer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class BookingServiceTest extends BaseTest {

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private PricingClient pricingClient;
    @Autowired
    private PaymentClient paymentClient;


    @Test
    void testStartBooking_successfulFlow() {
        BookingRequest request = new BookingRequest("user1", "show1", List.of("A1", "A2"));

        // Mock inventory client holdSeats
        SeatHoldResponse seatHoldResponse = new SeatHoldResponse();
        seatHoldResponse.setStatus(OperationStatus.SUCCESS);
        seatHoldResponse.setSeatType(null);
        when(inventoryClient.holdSeats(any())).thenReturn(seatHoldResponse);

        // Mock pricing client
        ShowPriceCalculationResponse pricingResponse = new ShowPriceCalculationResponse();
        pricingResponse.setPrice(BigDecimal.valueOf(500));
        pricingResponse.setCurrency(Currency.getInstance("INR"));
        when(pricingClient.calculatePrice(any())).thenReturn(pricingResponse);

        // Mock payment client
        PaymentRecordCreationResponse paymentResponse = new PaymentRecordCreationResponse();
        paymentResponse.setStatus(OperationStatus.SUCCESS);
        paymentResponse.setSystemCode("pay123");
        when(paymentClient.createPaymentRecord(any())).thenReturn(paymentResponse);

        BookingResponse response = bookingService.startBooking(request);

        assertTrue(response.isSuccess());
        assertEquals(BookingStatus.PAYMENT_PENDING, response.getBookingStatus());
        assertNotNull(response.getPaymentInfo());
        assertEquals(BigDecimal.valueOf(500), response.getPaymentInfo().getAmount());
    }

    @Test
    void testStartBooking_seatHoldFails() {
        BookingRequest request = new BookingRequest("user1", "show1", List.of("A1", "A2"));

        when(inventoryClient.holdSeats(any())).thenReturn(null);

        BookingResponse response = bookingService.startBooking(request);
        assertFalse(response.isSuccess());
        assertEquals("Seat hold service unavailable", response.getMessage());
    }

    @Test
    void testCompletePayment_successfulFlow() {
        Booking booking = new Booking("user1", "show1", List.of("A1", "A2"), BookingStatus.PAYMENT_PENDING);
        booking.setAmount(BigDecimal.valueOf(500));
        bookingRepository.save(booking);

        CompletePaymentRequest request = new CompletePaymentRequest(
                booking.getSystemCode(),
                new PaymentDetails("pay123", BigDecimal.valueOf(500), PaymentStatus.SUCCESS)
        );

        SeatConfirmResponse confirmResponse = new SeatConfirmResponse();
        confirmResponse.setStatus(OperationStatus.SUCCESS);
        when(inventoryClient.confirmSeats(any())).thenReturn(confirmResponse);

        BookingResponse response = bookingService.completePayment(request);

        assertTrue(response.isSuccess());
        assertEquals(BookingStatus.CONFIRMED.name(), bookingRepository.findBySystemCode(booking.getSystemCode()).get().getStatus().name());
    }

    @Test
    void testCompletePayment_paymentFailed() {
        Booking booking = new Booking("user1", "show1", List.of("A1", "A2"), BookingStatus.PAYMENT_PENDING);
        booking.setAmount(BigDecimal.valueOf(500));
        bookingRepository.save(booking);

        CompletePaymentRequest request = new CompletePaymentRequest(
                booking.getSystemCode(),
                new PaymentDetails("pay123", BigDecimal.valueOf(500), PaymentStatus.FAILED)
        );

        SeatReleaseResponse releaseResponse = new SeatReleaseResponse();
        releaseResponse.setStatus(SeatReleaseStatus.SUCCESS);
        when(inventoryClient.releaseSeats(any())).thenReturn(releaseResponse);

        BookingResponse response = bookingService.completePayment(request);

        assertFalse(response.isSuccess());
        assertEquals(BookingStatus.PAYMENT_FAILED, bookingRepository.findBySystemCode(booking.getSystemCode()).get().getStatus());
    }

    @Test
    void testCompletePayment_seatConfirmationFails() {
        Booking booking = new Booking("user1", "show1", List.of("A1", "A2"), BookingStatus.PAYMENT_PENDING);
        booking.setAmount(BigDecimal.valueOf(500));
        bookingRepository.save(booking);

        CompletePaymentRequest request = new CompletePaymentRequest(
                booking.getSystemCode(),
                new PaymentDetails("pay123", BigDecimal.valueOf(500), PaymentStatus.SUCCESS)
        );

        SeatConfirmResponse confirmResponse = new SeatConfirmResponse();
        confirmResponse.setStatus(OperationStatus.FAILURE);
        when(inventoryClient.confirmSeats(any())).thenReturn(confirmResponse);

        BookingResponse response = bookingService.completePayment(request);

        assertFalse(response.isSuccess());
        assertEquals(BookingStatus.SEAT_CONFIRMATION_FAILED, bookingRepository.findBySystemCode(booking.getSystemCode()).get().getStatus());
    }

}
