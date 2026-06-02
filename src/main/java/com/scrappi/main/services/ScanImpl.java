package com.scrappi.main.services;

import com.scrappi.main.dto.ScanReq;
import com.scrappi.main.dto.ScanRes;


public interface ScanImpl {
    Long createScan(ScanReq req);
    ScanRes getScan(Long scanId);
}
