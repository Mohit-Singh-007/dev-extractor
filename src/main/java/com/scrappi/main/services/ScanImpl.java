package com.scrappi.main.services;

import com.scrappi.main.dto.ScanReq;
import com.scrappi.main.dto.ScanRes;

import java.util.Optional;

public interface ScanImpl {
    Long createScan(ScanReq req);
    ScanRes getScan(Long scanId);
}
