package com.example.relayservice.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListenerService {
    public MessageListener<String, String> createMessageListener(String consumerName, String groupId) {
        return new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> record) {
                // Print the consumer details and message when consumed
                System.out.println("Consumer Name: " + consumerName);
                System.out.println("Consumer Group ID: " + groupId);
                System.out.println("Message received from topic: " + record.topic() + " | Message: " + record.value());

                // Any other processing logic you want to trigger on message consumption
            }
        };
    }
}
