package com.scrappi.main.queue;

import com.rabbitmq.client.Channel;
import com.scrappi.main.background.ScanProcessing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class Consumer {

    private final ScanProcessing scanProcessing;
    private final Producer producer;

    private static final int MAX_RETRIES=3;

    @RabbitListener(queues = RabbitMQConfig.SCAN_QUEUE)
    public void consume(Long scanId, Message message, Channel channel) throws IOException {

        int count = getRetryCount(message);

        try {
            scanProcessing.process(scanId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("Scan {} processed successfully", scanId);
        } catch (Exception e) {
            log.error("Scan {} failed, retry count: {}", scanId, count);

            if(count >= MAX_RETRIES){
                // max retries reached — send to DLQ
                log.error("Scan {} exhausted retries, sending to DLQ", scanId);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }else{
                // ack original, publish to retry queue with incremented count
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                publishRetry(scanId, count + 1);
            }

        }

        scanProcessing.process(scanId);
    }
    private void publishRetry(Long scanId,int count){
        producer.publishToRetry(scanId,count);
    }

    private int getRetryCount(Message message){
        Object count = message.getMessageProperties().getHeaders().get("x-retry-count");
        return count == null ? 0 : (int) count;
    }
}
