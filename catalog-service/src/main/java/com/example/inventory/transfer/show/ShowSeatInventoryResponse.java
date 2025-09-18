package com.example.inventory.transfer.show;

import com.example.inventory.enumeration.SeatInventoryStatus;
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