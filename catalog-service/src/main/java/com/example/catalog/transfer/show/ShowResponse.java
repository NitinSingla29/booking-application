package com.example.catalog.transfer.show;

import com.example.catalog.enumeration.ShowStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShowResponse {
    private Long id;
    private String systemCode;
    private String movieSystemCode;
    private String screenSystemCode;
    private String theatreSystemCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate showDate;
    private ShowStatus showStatus;
}