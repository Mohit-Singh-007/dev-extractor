package com.scrappi.main.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {
    public static final String EVENT_EXCHANGE = "scan.event.exchange";
    public static final String STATUS_QUEUE = "scan.status.queue";

    @Bean
    public Queue statusQueue(){
        return QueueBuilder.durable(STATUS_QUEUE).build();
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(EVENT_EXCHANGE);
    }

    @Bean
    public Binding statusBinding(Queue queue,FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }
}
