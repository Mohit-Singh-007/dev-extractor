package com.scrappi.main.services;

import com.scrappi.main.dto.scan.ScanReq;
import com.scrappi.main.dto.scan.ScanRes;


public interface ScanImpl {
    Long createScan(ScanReq req);
    ScanRes getScanById(Long scanId);
}
