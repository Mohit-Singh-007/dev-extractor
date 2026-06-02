package com.scrappi.main.services.impl;

import com.scrappi.main.dto.FontRes;
import com.scrappi.main.dto.ScanReq;
import com.scrappi.main.dto.ScanRes;
import com.scrappi.main.dto.TechRes;
import com.scrappi.main.model.Font;
import com.scrappi.main.model.Scan;
import com.scrappi.main.model.ScanStatus;
import com.scrappi.main.queue.Producer;
import com.scrappi.main.repository.ScanRepository;
import com.scrappi.main.services.ScanImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ScanService implements ScanImpl {

    private final ScanRepository scanRepository;
    private final Producer producer;
//    private final ScanProcessing scanProcessing;

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

        // BG-process - step [@Async]
        // scanProcessing.process(scan.getId());
        // rabbit-mq
        producer.publish(scan.getId());

        return scan.getId();
    }

    public ScanRes getScan(Long id) {

        Scan scan = scanRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Scan not found"));

        List<TechRes> res = scan.getTechnologies().stream().map(tech ->
                new TechRes(tech.getId(),tech.getName())).toList();

        Map<String,List<Font>> fonts = scan.getFonts().stream().collect(
                Collectors.groupingBy(Font::getFamily)
        );
        List<FontRes> fontRes =
                fonts.entrySet()
                        .stream()
                        .map(entry -> {

                            Set<Integer> weights =
                                    entry.getValue()
                                            .stream()
                                            .map(Font::getWeight)
                                            .collect(Collectors.toSet());

                            List<String> urls =
                                    entry.getValue()
                                            .stream()
                                            .map(Font::getFileUrl)
                                            .filter(Objects::nonNull)
                                            .distinct()
                                            .toList();

                            return new FontRes(
                                    entry.getKey(),
                                    weights,
                                    urls
                            );
                        })
                        .toList();

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
}
