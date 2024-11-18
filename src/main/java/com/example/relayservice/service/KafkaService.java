package com.example.relayservice.service;

import com.example.relayservice.entity.Topic;

import java.util.List;

public interface KafkaService {
    public List<String> listTopics();
    public boolean isKafkaRunning();
    public void createMultipleTopics(List<Topic> topics);

}
