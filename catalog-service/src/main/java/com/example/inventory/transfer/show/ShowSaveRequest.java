package com.example.inventory.transfer.show;

import com.example.inventory.enumeration.ShowStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShowSaveRequest {
    private String movieSystemCode;
    private String screenSystemCode;
    private String theatreSystemCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate showDate;
    private ShowStatus showStatus;
}