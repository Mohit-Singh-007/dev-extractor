package com.scrappi.main.queue.status;

import com.scrappi.main.dto.event.StatusEvent;
import com.scrappi.main.queue.EventConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusConsumer {

    @RabbitListener(queues = EventConfiguration.STATUS_QUEUE)
    public void consume(StatusEvent event){
        log.info("[STATUS] scanId={} status={} message={} at={}",
                event.scanId(),
                event.status(),
                event.message(),
                event.timestamp()
        );

        // email - or any other work here later
    }
}
