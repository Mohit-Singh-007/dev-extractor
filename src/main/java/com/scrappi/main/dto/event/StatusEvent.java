package com.scrappi.main.dto.event;

import com.scrappi.main.model.ScanStatus;

import java.time.LocalDateTime;

public record StatusEvent(
        Long scanId,
        ScanStatus status,
        String message,
        LocalDateTime timestamp
) {}
