package com.scrappi.main.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Producer {


    private final RabbitTemplate template;

    public void publish(Long scanId){
        template.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                scanId,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                });

        log.info("PUBLISHED SCAN ID: {}",scanId);
    }

    public void publishToRetry(Long scanId,int retryCount){
        template.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "retry.key.#",
                scanId,
                message -> {
                    message.getMessageProperties().setHeader("x-retry-count",retryCount);
                    return message;
                }
                );
        log.info("SCAN ID {} PUBLISHED TO RETRY QUEUE for RETRY: {}",scanId,retryCount);
    }

}
