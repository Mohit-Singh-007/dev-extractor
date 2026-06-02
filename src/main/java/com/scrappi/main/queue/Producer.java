package com.scrappi.main.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Producer {


    private final RabbitTemplate template;

    public void publish(Long scanId){
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"routing.key",scanId);
    }

}
