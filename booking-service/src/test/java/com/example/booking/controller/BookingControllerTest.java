package com.example.booking.controller;

import com.example.booking.BaseTest;
import com.example.booking.BookingStatus;
import com.example.booking.client.InventoryClient;
import com.example.booking.client.PricingClient;
import com.example.booking.client.transfer.inventory.SeatHoldResponse;
import com.example.booking.client.transfer.pricing.ShowPriceCalculationResponse;
import com.example.booking.domain.jpa.Booking;
import com.example.booking.repository.jpa.IBookingRepository;
import com.example.booking.transfer.BookingRequest;
import com.example.booking.transfer.CompletePaymentRequest;
import com.example.booking.transfer.PaymentDetails;
import com.example.core.enumeration.OperationStatus;
import com.example.core.enumeration.PaymentStatus;
import com.example.core.enumeration.SeatType;
import com.example.payment.client.PaymentClient;
import com.example.payment.transfer.PaymentRecordCreationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookingControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private PricingClient pricingClient;

    @Autowired
    private PaymentClient paymentClient;


    @BeforeEach
    void setupMocks() {
        // Mock seat hold success
        SeatHoldResponse seatHoldResponse = new SeatHoldResponse();
        seatHoldResponse.setStatus(OperationStatus.SUCCESS);
        seatHoldResponse.setSeatType(SeatType.REGULAR);
        Mockito.when(inventoryClient.holdSeats(Mockito.any())).thenReturn(seatHoldResponse);

        // Mock price calculation success
        ShowPriceCalculationResponse priceResponse = new ShowPriceCalculationResponse();
        priceResponse.setPrice(new BigDecimal("100.00"));
        priceResponse.setCurrency(Currency.getInstance("USD"));
        Mockito.when(pricingClient.calculatePrice(Mockito.any())).thenReturn(priceResponse);

        // Mock payment record creation success
        PaymentRecordCreationResponse paymentResponse = new PaymentRecordCreationResponse();
        paymentResponse.setStatus(OperationStatus.SUCCESS);
        paymentResponse.setSystemCode("PAYMENT123");
        Mockito.when(paymentClient.createPaymentRecord(Mockito.any())).thenReturn(paymentResponse);
    }

    @Test
    void testStartBooking_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setUserSystemCode("USER1");
        request.setShowSystemCode("SHOW123");
        request.setSeatCodes(Arrays.asList("A1", "A2"));

        mockMvc.perform(post("/booking/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.bookingSystemCode").exists())
                .andExpect(jsonPath("$.paymentInfo").exists());
    }

    @Test
    void testCompletePayment_Success() throws Exception {
        // Create and save a booking with systemCode "BOOKING123"
        Booking booking = new Booking("USER1", "SHOW123",
                Arrays.asList("A1", "A2"), BookingStatus.PAYMENT_PENDING);
        booking.setAmount(new BigDecimal("100.00"));
        booking.setCurrency(Currency.getInstance("USD"));
        bookingRepository.save(booking);

        CompletePaymentRequest request = new CompletePaymentRequest();
        request.setBookingSystemCode(booking.getSystemCode());
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPaymentRecordId("PAYMENT123");
        paymentDetails.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentDetails.setPaymentAmount(new BigDecimal("100.00"));
        request.setPaymentDetails(paymentDetails);

        mockMvc.perform(post("/booking/complete-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }
}