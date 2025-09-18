package com.example.catalog.transfer.show;

import com.example.catalog.enumeration.SeatInventoryStatus;
import lombok.Data;

import java.util.List;

@Data
public class ShowSeatInventoryResponse {
    private List<SeatInfo> seats;

    @Data
    public static class SeatInfo {
        private String seatSystemCode;
        private String seatCode;
        private SeatInventoryStatus seatStatus;
    }
}