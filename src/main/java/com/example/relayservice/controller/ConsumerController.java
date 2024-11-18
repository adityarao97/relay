package com.example.relayservice.controller;

import com.example.relayservice.entity.Consumer;
import com.example.relayservice.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/consumer")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @PostMapping
    public List<Consumer> createConsumer(@RequestBody List<Consumer> consumers) {
        return consumerService.createConsumer(consumers);
    }

    @GetMapping
    public List<Consumer> getConsumerListByTopicName(@RequestParam String topicName) {
        return consumerService.getConsumerListByTopicName(topicName);
    }
}
