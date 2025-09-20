package com.example.booking.transfer;

import com.example.booking.BookingStatus;
import com.example.booking.enumeration.OperationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookingResponse {
    private String bookingSystemCode;
    private String showSystemCode;
    private PaymentInfo paymentInfo;
    private List<String> seatCodes;
    private BookingStatus bookingStatus;
    private OperationStatus status;
    private String message;

    public static BookingResponse success(String bookingCode, String showCode, List<String> seatCodes, BookingStatus bookingStatus, PaymentInfo paymentInfo) {
        BookingResponse response = new BookingResponse();
        response.setBookingSystemCode(bookingCode);
        response.setShowSystemCode(showCode);
        response.setSeatCodes(seatCodes);
        response.setPaymentInfo(paymentInfo);
        response.setStatus(OperationStatus.SUCCESS);
        return response;
    }

    public static BookingResponse fail(String message) {
        BookingResponse response = new BookingResponse();
        response.setStatus(OperationStatus.FAILURE);
        response.setMessage(message);
        return response;
    }

    public boolean isSuccess() {
        return OperationStatus.SUCCESS == this.status;
    }
}
