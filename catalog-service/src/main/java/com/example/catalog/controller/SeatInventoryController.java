package com.example.catalog.controller;

import com.example.catalog.service.SeatInventoryService;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.client.SeatHoldResponse;
import com.example.catalog.transfer.client.SeatReleaseRequest;
import com.example.catalog.transfer.client.SeatReleaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/inventory/seat")
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

}
