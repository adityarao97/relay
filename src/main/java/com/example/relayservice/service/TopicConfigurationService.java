package com.example.relayservice.service;

import com.example.relayservice.entity.Topic;

import java.util.List;

public interface TopicConfigurationService {
    public List<Topic> getTopicsByConfigName(String kafkaConfigName);
    public List<Topic> getTopicsByConfigId(int kafkaConfigId);
    public Topic getTopicByName(String topicName);
    public List<Topic> saveTopics(List<Topic> topic);
    public void sendMessageToKafka(String topicName, String message);
}