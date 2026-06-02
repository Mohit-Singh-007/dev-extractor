package com.scrappi.main.controller;

import com.scrappi.main.queue.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitMQController {

    private final Producer producer;

    @PostMapping("/send")
    public String sendMsg(@RequestParam String msg){
//        producer.sendMessage(msg);
        return "Message send: "+msg;
    }
}
