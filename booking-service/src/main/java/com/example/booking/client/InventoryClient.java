package com.example.booking.client;

import com.example.booking.client.transfer.inventory.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "${services.inventory.url}/inventory/seat")
public interface InventoryClient {
    @PostMapping("/hold-seats")
    SeatHoldResponse holdSeats(@RequestBody SeatHoldRequest request);

    @PostMapping("/release-seats")
    SeatReleaseResponse releaseSeats(SeatReleaseRequest releaseRequest);

    @PostMapping("/confirm-seats")
    SeatConfirmResponse confirmSeats(@RequestBody SeatConfirmRequest seatConfirmRequest);
}
