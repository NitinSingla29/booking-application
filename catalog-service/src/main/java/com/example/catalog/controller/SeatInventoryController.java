package com.example.catalog.controller;

import com.example.catalog.service.SeatInventoryService;
import com.example.catalog.transfer.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory/seat")
public class SeatInventoryController {

    @Autowired
    private SeatInventoryService seatInventoryService;

    @PostMapping("/hold-seats")
    public ResponseEntity<SeatHoldResponse> holdSeats(@RequestBody SeatHoldRequest request) {
        SeatHoldResponse response = seatInventoryService.holdSeats(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/release-seats")
    public ResponseEntity<SeatReleaseResponse> releaseSeats(@RequestBody SeatReleaseRequest request) {
        SeatReleaseResponse response = seatInventoryService.releaseSeats(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-seats")
    public ResponseEntity<SeatConfirmResponse> confirmSeats(@RequestBody SeatConfirmRequest request) {
        SeatConfirmResponse response = seatInventoryService.confirmSeats(request);
        return ResponseEntity.ok(response);
    }

}
