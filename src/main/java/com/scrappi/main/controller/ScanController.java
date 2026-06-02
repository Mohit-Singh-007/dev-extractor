package com.scrappi.main.controller;

import com.scrappi.main.dto.ScanReq;
import com.scrappi.main.dto.ScanRes;
import com.scrappi.main.services.impl.ScanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scans")
public class ScanController {
    private final ScanService scanService;

    @PostMapping
    public ResponseEntity<Long> createScan(
            @RequestBody @Valid ScanReq request) {

        Long scanId = scanService.createScan(request);

        return ResponseEntity.ok(scanId);
    }

    @GetMapping("/{id}")
    public ScanRes getScan(
            @PathVariable Long id) {

        return scanService.getScanById(id);
    }
}
