package com.example.inventory.controller;


import com.example.inventory.service.TheatreService;
import com.example.inventory.transfer.theatre.TheatreResponse;
import com.example.inventory.transfer.theatre.TheatreSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/theatres")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @PostMapping
    public ResponseEntity<TheatreResponse> createTheatre(@RequestBody TheatreSaveRequest request) {
        TheatreResponse response = theatreService.saveTheatre(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{systemCode}")
    public ResponseEntity<TheatreResponse> getTheatre(@PathVariable String systemCode) {
        TheatreResponse response = theatreService.getTheatreBySystemCode(systemCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{systemCode}")
    public ResponseEntity<TheatreResponse> updateTheatre(
            @PathVariable String systemCode,
            @RequestBody TheatreSaveRequest request) {
        TheatreResponse response = theatreService.updateTheatreBySystemCode(systemCode, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{systemCode}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable String systemCode) {
        theatreService.deleteTheatreBySystemCode(systemCode);
        return ResponseEntity.noContent().build();
    }
}
