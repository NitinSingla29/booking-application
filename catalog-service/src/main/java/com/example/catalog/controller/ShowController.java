package com.example.catalog.controller;

import com.example.catalog.service.ShowService;
import com.example.catalog.transfer.show.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@RequestBody ShowSaveRequest request) {
        ShowResponse response = showService.saveShow(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{systemCode}")
    public ResponseEntity<ShowResponse> getShow(@PathVariable String systemCode) {
        ShowResponse response = showService.getShowBySystemCode(systemCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{systemCode}")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable String systemCode, @RequestBody ShowUpdateRequest request) {
        request.setSystemCode(systemCode);
        ShowResponse response = showService.updateShowBySystemCode(request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{systemCode}/seats")
    public ResponseEntity<ShowSeatInventoryResponse> getSeatInventory(@PathVariable String systemCode, @RequestBody ShowSeatInventoryRequest request) {
        request.setShowSystemCode(systemCode);
        ShowSeatInventoryResponse response = showService.getSeatInventoryForShow(request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}