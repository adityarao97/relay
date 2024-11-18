package com.example.relayservice.service;

import com.example.relayservice.entity.Consumer;
import com.example.relayservice.entity.Topic;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

public interface ConsumerService {
//    public void subscribeTopics(int kafkaConfigId);
    public void consumeMessage(String message);
    public List<Consumer> createConsumer(List<Consumer> consumer);

    public List<Consumer> getConsumerListByConfigId(int configId);
    public List<Consumer> getConsumerListByTopicName(String topicName);
}
