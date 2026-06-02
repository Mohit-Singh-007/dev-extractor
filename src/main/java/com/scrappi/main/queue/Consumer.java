package com.scrappi.main.queue;

import com.scrappi.main.background.ScanProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Consumer {

    private final ScanProcessing scanProcessing;
    @RabbitListener(queues = RabbitMQConfig.SCAN_QUEUE)
    public void consume(Long scanId){
        scanProcessing.process(scanId);
    }
}
