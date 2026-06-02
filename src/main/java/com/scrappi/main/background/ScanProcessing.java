package com.scrappi.main.background;

import com.scrappi.main.dto.ExtractedFont;
import com.scrappi.main.model.Font;
import com.scrappi.main.model.Scan;
import com.scrappi.main.model.ScanStatus;
import com.scrappi.main.model.Technology;
import com.scrappi.main.repository.FontRepository;
import com.scrappi.main.repository.ScanRepository;
import com.scrappi.main.repository.TechRepository;
import com.scrappi.main.utils.TechnologyDetector;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


// later using rabbitmq | kafka
@Service
@RequiredArgsConstructor
public class ScanProcessing {

    private final ScanRepository scanRepository;
    private final TechnologyDetector technologyDetector;
    private final TechRepository techRepository;
    private final FontRepository fontRepository;
    private final FontExtractor fontExtractor;


   // @Async -> rabbit mq provides async behaviour
    public void process(Long scanId)  {
        Scan scan = scanRepository.findById(scanId).orElseThrow();


        scan.setStatus(ScanStatus.PROCESSING);
        scanRepository.save(scan);

        try{
            Document document = Jsoup.connect(scan.getUrl()).get();

            String html = document.outerHtml();
            List<String> detectedTech = technologyDetector.detect(html);
            List<Technology> technologies = detectedTech.stream().map(name ->
                    Technology.builder().name(name).scan(scan).build()).toList();

            List<ExtractedFont> extractedFonts =
                    fontExtractor.extract(document);
            List<Font> fonts =
                    extractedFonts.stream()
                            .map(font ->
                                    Font.builder()
                                            .family(font.family())
                                            .weight(font.weight())
                                            .fileUrl(font.fileUrl())
                                            .source(font.source())
                                            .scan(scan)
                                            .build()
                            )
                            .toList();
            techRepository.saveAll(technologies);
            fontRepository.saveAll(fonts);

            String title = document.title();
            String description = document.select("meta[name=description]").attr("content");
            int totalLinks = document.select("a[href]").size();

            int totalImages = document.select("img").size();

            int totalScripts = document.select("script").size();

            scan.setTitle(title);
            scan.setDescription(description);
            scan.setStatus(ScanStatus.COMPLETED);
            scan.setCompletedAt(LocalDateTime.now());
            scan.setTotalLinks(totalLinks);
            scan.setTotalImages(totalImages);
            scan.setTotalScripts(totalScripts);

        }catch (Exception ex){
            scan.setStatus(ScanStatus.FAILED);
        }

        scanRepository.save(scan);
    }


}
