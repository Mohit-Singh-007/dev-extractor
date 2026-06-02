package com.scrappi.main.queue;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String SCAN_QUEUE = "scan.queue";
    public static final String EXCHANGE_NAME = "scan.exchange";
    @Bean
    public Queue queue(){
        return new Queue(SCAN_QUEUE,true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue , TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("routing.key.#");
    }
}
