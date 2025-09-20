package com.example.booking.service;

import com.example.booking.BookingStatus;
import com.example.booking.client.InventoryClient;
import com.example.booking.client.PaymentClient;
import com.example.booking.client.PricingClient;
import com.example.booking.client.transfer.inventory.*;
import com.example.booking.client.transfer.payment.PaymentRecordCreationRequest;
import com.example.booking.client.transfer.payment.PaymentRecordCreationResponse;
import com.example.booking.client.transfer.pricing.ShowPriceCalculationRequest;
import com.example.booking.client.transfer.pricing.ShowPriceCalculationResponse;
import com.example.booking.domain.jpa.Booking;
import com.example.booking.enumeration.OperationStatus;
import com.example.booking.enumeration.PaymentStatus;
import com.example.booking.enumeration.SeatReleaseStatus;
import com.example.booking.repository.jpa.IBookingRepository;
import com.example.booking.transfer.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class.getName());
    private final IBookingRepository bookingRepository;
    private final InventoryClient inventoryClient;
    private final PricingClient pricingClient;
    private final PaymentClient paymentClient;

    /**
     * Step 1: Start booking by holding seats, calculating price, and creating payment record
     */
    public BookingResponse startBooking(BookingRequest request) {
        Booking booking = createInitialBooking(request);

        // Hold seats
        SeatHoldResponse seatHoldResponse = holdSeats(request, booking);
        if (seatHoldResponse == null) {
            return failBooking(booking, BookingStatus.INVENTORY_CHECK_FAILED, "Seat hold service unavailable");
        }
        if (OperationStatus.FAILURE == seatHoldResponse.getStatus()) {
            return failBooking(booking, BookingStatus.SEAT_NOT_AVAILABLE, "Some seats are not available");
        }
        updateBookingStatus(booking, BookingStatus.SEAT_HOLDED, null);

        // Calculate price
        ShowPriceCalculationResponse pricingResponse = calculatePrice(request, seatHoldResponse);
        if (pricingResponse == null) {
            return failBooking(booking, BookingStatus.PRICING_SERVICE_UNAVAILABLE, "Pricing service unavailable");
        }
        booking.setAmount(pricingResponse.getPrice());
        booking.setCurrency(pricingResponse.getCurrency());
        updateBookingStatus(booking, BookingStatus.PRICE_CALCULATED, null);

        // Create payment record
        PaymentRecordCreationResponse paymentRecordResponse = createPaymentRecord(request, booking, pricingResponse);
        if (paymentRecordResponse == null || OperationStatus.FAILURE == paymentRecordResponse.getStatus()) {
            return failBooking(booking, BookingStatus.PAYMENT_RECORD_CREATION_FAILED, "Payment record creation failed");
        }

        // Payment pending
        updateBookingStatus(booking, BookingStatus.PAYMENT_PENDING, paymentRecordResponse.getSystemCode());

        // Prepare response
        PaymentInfo paymentInfo = new PaymentInfo(paymentRecordResponse.getSystemCode(), pricingResponse.getPrice(), pricingResponse.getCurrency());

        return BookingResponse.success(booking.getSystemCode(), request.getShowSystemCode(), request.getSeatCodes(), booking.getStatus(), paymentInfo);
    }

    @Transactional
    public BookingResponse completePayment(CompletePaymentRequest request) {
        // Fetch booking
        Booking booking = bookingRepository.findBySystemCode(request.getBookingSystemCode())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        PaymentDetails paymentDetails = request.getPaymentDetails();

        // Validate payment
        if (paymentDetails.getPaymentStatus() != PaymentStatus.SUCCESS ||
                booking.getAmount().compareTo(paymentDetails.getPaymentAmount()) != 0) {
            releaseSeats(booking);
            updateBookingStatus(booking, BookingStatus.PAYMENT_FAILED, paymentDetails.getPaymentRecordId());
            return BookingResponse.fail("Payment failed or amount mismatch");
        }

        // Payment successful → confirm seats
        boolean seatsConfirmed = confirmSeats(booking);
        if (!seatsConfirmed) {
            updateBookingStatus(booking, BookingStatus.SEAT_CONFIRMATION_FAILED, paymentDetails.getPaymentRecordId());
            return BookingResponse.fail("Failed to confirm seats after payment");
        }

        //  Booking confirmed
        updateBookingStatus(booking, BookingStatus.CONFIRMED, paymentDetails.getPaymentRecordId());
        return BookingResponse.success(booking.getSystemCode(), booking.getShowSystemCode(), booking.getSeatCodes(), booking.getStatus(), null);
    }



    /* ==================== Private Helper Methods ==================== */

    private void releaseSeats(Booking booking) {
        try {
            SeatReleaseRequest releaseRequest = new SeatReleaseRequest(booking.getSystemCode());

            SeatReleaseResponse response = inventoryClient.releaseSeats(releaseRequest);

            if (response == null || SeatReleaseStatus.FAILURE == response.getStatus()) {
                // Log a warning if seats could not be released
                // Seats may be released later via scheduled job or retry
                LOGGER.warn("Failed to release seats for booking: {}", booking.getSystemCode());
            }
        } catch (Exception e) {
            // Catch exceptions to prevent crashing the flow
            LOGGER.error("Exception while releasing seats for booking {}: {}", booking.getSystemCode(), e.getMessage(), e);
        }
    }

    private boolean confirmSeats(Booking booking) {
        try {
            SeatConfirmRequest confirmRequest = new SeatConfirmRequest(booking.getSystemCode());

            SeatConfirmResponse response = inventoryClient.confirmSeats(confirmRequest);

            if (response == null || OperationStatus.FAILURE == response.getStatus()) {
                LOGGER.warn("Failed to confirm seats for booking: {}", booking.getSystemCode());
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception while confirming seats for booking {}: {}", booking.getSystemCode(), e.getMessage(), e);
            return false;
        }
    }


    private Booking createInitialBooking(BookingRequest request) {
        Booking booking = new Booking(
                request.getUserSystemCode(),
                request.getShowSystemCode(),
                request.getSeatCodes(),
                BookingStatus.INITIATED
        );
        return bookingRepository.save(booking);
    }

    private SeatHoldResponse holdSeats(BookingRequest request, Booking booking) {
        try {
            SeatHoldRequest seatHoldRequest = new SeatHoldRequest(
                    request.getShowSystemCode(),
                    request.getSeatCodes(),
                    booking.getSystemCode(),
                    request.getUserSystemCode()
            );
            return inventoryClient.holdSeats(seatHoldRequest);
        } catch (Exception e) {
            return null;
        }
    }

    private ShowPriceCalculationResponse calculatePrice(BookingRequest request, SeatHoldResponse seatHoldResponse) {
        try {
            ShowPriceCalculationRequest priceRequest = new ShowPriceCalculationRequest();
            priceRequest.setUserSystemCode(request.getUserSystemCode());
            priceRequest.setShowSystemCode(request.getShowSystemCode());
            priceRequest.setSeatType(seatHoldResponse.getSeatType());
            priceRequest.setQuantity(request.getSeatCodes().size());
            return pricingClient.calculatePrice(priceRequest);
        } catch (Exception e) {
            return null;
        }
    }

    private PaymentRecordCreationResponse createPaymentRecord(
            BookingRequest request, Booking booking, ShowPriceCalculationResponse pricingResponse) {
        try {
            PaymentRecordCreationRequest paymentRequest = new PaymentRecordCreationRequest();
            paymentRequest.setUserSystemCode(request.getUserSystemCode());
            paymentRequest.setSourceObjectType("BOOKING");
            paymentRequest.setSourceObjectCode(booking.getSystemCode());
            paymentRequest.setAmount(pricingResponse.getPrice());
            paymentRequest.setCurrency(pricingResponse.getCurrency());

            return paymentClient.createPaymentRecord(paymentRequest);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateBookingStatus(Booking booking, BookingStatus status, String paymentRecordSystemCode) {
        booking.setStatus(status);
        booking.setPaymentRecordSystemCode(paymentRecordSystemCode);
        bookingRepository.save(booking);
    }

    private BookingResponse failBooking(Booking booking, BookingStatus status, String message) {
        booking.setStatus(status);
        bookingRepository.save(booking);
        return BookingResponse.fail(message);
    }
}

