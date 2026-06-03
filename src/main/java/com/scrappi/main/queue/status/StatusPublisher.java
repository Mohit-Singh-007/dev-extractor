package com.scrappi.main.queue.status;

import com.scrappi.main.dto.event.StatusEvent;
import com.scrappi.main.model.ScanStatus;
import com.scrappi.main.queue.EventConfiguration;
import com.scrappi.main.queue.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusPublisher {

    private final RabbitTemplate template;

    public void publish(Long scanId, ScanStatus status,String message){
        StatusEvent res = new StatusEvent(
                scanId,
                status,
                message,
                LocalDateTime.now()
        );

        template.convertAndSend(EventConfiguration.EVENT_EXCHANGE,"",res);
        log.info("Status event published: scanId={} status={}", scanId, status);
    }
}

