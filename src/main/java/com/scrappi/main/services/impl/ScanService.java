package com.scrappi.main.services.impl;

import com.scrappi.main.dto.FontRes;
import com.scrappi.main.dto.ScanReq;
import com.scrappi.main.dto.ScanRes;
import com.scrappi.main.dto.TechRes;
import com.scrappi.main.model.Font;
import com.scrappi.main.model.Scan;
import com.scrappi.main.model.ScanStatus;
import com.scrappi.main.model.Technology;
import com.scrappi.main.queue.Producer;
import com.scrappi.main.repository.ScanRepository;
import com.scrappi.main.services.ScanImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class ScanService implements ScanImpl {

    private final ScanRepository scanRepository;
    private final Producer producer;

    public ScanService(ScanRepository scanRepository, Producer producer) {
        this.scanRepository = scanRepository;
        this.producer = producer;
    }

    @Override
    public Long createScan(ScanReq req) {
        Scan scan = new Scan();
        scan.setUrl(req.url());
        scan.setStatus(ScanStatus.PENDING);
        scan.setCreatedAt(LocalDateTime.now());

        scanRepository.save(scan);

        // rabbit-mq __ need retries
        producer.publish(scan.getId());

        return scan.getId();
    }

    public ScanRes getScanById(Long id) {

        Scan scan = scanRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Scan not found"));

        List<TechRes> res = mapToTechRES(scan.getTechnologies());

        List<FontRes> fontRes = mapToFontRES(scan.getFonts());

        return new ScanRes(
                scan.getId(),
                scan.getUrl(),
                scan.getStatus(),
                scan.getTitle(),
                scan.getDescription(),
                scan.getTotalLinks(),
                scan.getTotalImages(),
                scan.getTotalScripts(),
                res,
                fontRes
        );
    }

    private List<TechRes> mapToTechRES(List<Technology> technology){
        return technology
                .stream()
                .map(tech -> new TechRes(tech.getId(),tech.getName())).toList();
    }

    private List<FontRes> mapToFontRES(List<Font> fonts){
        return fonts
                .stream()
                .collect(Collectors.groupingBy(Font::getFamily))
                .entrySet()
                .stream()
                .map(this::fontMapper)
                .toList();
    }
    private FontRes fontMapper(Map.Entry<String,List<Font>> entry){
        Set<Integer> weights = entry.getValue().stream()
                .map(Font::getWeight)
                .collect(Collectors.toSet());

        List<String> urls = entry.getValue().stream()
                .map(Font::getFileUrl)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return new FontRes(entry.getKey(), weights, urls);
    }
}
