package com.scrappi.main.queue;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // main-queue
    public static final String SCAN_QUEUE = "scan.queue";
    public static final String EXCHANGE_NAME = "scan.exchange";
    public static final String ROUTING_KEY = "routing.key";

    // dlq
    public static final String SCAN_DLQ = "scan.dlq";
    public static final String SCAN_DLX = "scan.dlx";
    public static final String SCAN_DLQ_ROUTING_KEY = "scan.dlq.routing.key";

    // retry-queue
    public static final String SCAN_RETRY_QUEUE = "scan.retry.queue";

    // for proper routing i need QueueBuilder -> more fine control Queue-> simple task
    @Bean
    public Queue scanQueue(){
        return QueueBuilder.durable(SCAN_QUEUE)
                .withArgument("x-dead-letter-exchange",SCAN_DLX)
                .withArgument("x-dead-letter-routing-key",SCAN_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dlq(){
        return QueueBuilder.durable(SCAN_DLQ).build();
    }

    // retry with 30 sec delay
    @Bean
    public Queue retryQueue(){
        return QueueBuilder.durable(SCAN_RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .withArgument("x-message-ttl",30000)
                .build();
    }



    @Bean
    public TopicExchange scanExchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }
    @Bean
    public DirectExchange dlqExchange(){
        return new DirectExchange(SCAN_DLX);
    }


    @Bean
    public Binding scanBinding(@Qualifier("scanQueue") Queue queue , TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY+".#");
    }

    @Bean
    public Binding dlqBinding(@Qualifier("dlq") Queue queue,DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(SCAN_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding retryBinding(@Qualifier("retryQueue") Queue queue,TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("retry.key.#");
    }

}
