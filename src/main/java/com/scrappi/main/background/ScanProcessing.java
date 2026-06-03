package com.scrappi.main.background;

import com.scrappi.main.dto.font.ExtractedFont;
import com.scrappi.main.model.Font;
import com.scrappi.main.model.Scan;
import com.scrappi.main.model.ScanStatus;
import com.scrappi.main.queue.status.StatusPublisher;
import com.scrappi.main.repository.FontRepository;
import com.scrappi.main.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanProcessing {

    private final ScanRepository scanRepository;
    private final FontRepository fontRepository;
    private final FontExtractor fontExtractor;
    private final StatusPublisher publisher;


   // @Async -> rabbit mq provides async behaviour
    public void process(Long scanId)  {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(()-> new RuntimeException("ScanID not found"));


        scan.setStatus(ScanStatus.PROCESSING);
        scanRepository.save(scan);
        publisher.publish(scanId,ScanStatus.PROCESSING,"SCAN STARTED");

        try{
            Document document = Jsoup.connect(scan.getUrl()).get();


            List<ExtractedFont> extractedFonts =
                    fontExtractor.extract(document);

            List<Font> fonts =
                    extractedFonts.stream()
                            .map(font -> mapToFont(font,scan)).toList();

            fontRepository.saveAll(fonts);


            applyDocumentMetadata(scan,document);
            scan.setStatus(ScanStatus.COMPLETED);
            scan.setCompletedAt(LocalDateTime.now());
            scanRepository.save(scan);

            publisher.publish(scanId,ScanStatus.COMPLETED,"SCAN COMPLETED");

        }catch (Exception ex){
            log.error("Scan failed for id {}: {}", scanId, ex.getMessage(), ex);
            scan.setStatus(ScanStatus.FAILED);
            scanRepository.save(scan);
            publisher.publish(scanId, ScanStatus.FAILED, ex.getMessage());
            throw new RuntimeException(ex); // rethrow so Consumer can handle retry
        }


    }


    private void applyDocumentMetadata(Scan scan, Document document) {
        scan.setTitle(document.title());
        scan.setDescription(document.select("meta[name=description]").attr("content"));
        scan.setTotalLinks(document.select("a[href]").size());
        scan.setTotalImages(document.select("img").size());
        scan.setTotalScripts(document.select("script").size());
    }


    private Font mapToFont(ExtractedFont font , Scan scan){
        return Font.builder()
                .family(font.family())
                .weight(font.weight())
                .fileUrl(font.fileUrl())
                .source(font.source())
                .scan(scan)
                .build();
    }


}
