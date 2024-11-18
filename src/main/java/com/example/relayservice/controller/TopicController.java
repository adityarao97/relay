package com.example.relayservice.controller;

import com.example.relayservice.entity.Topic;
import com.example.relayservice.service.TopicConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private TopicConfigurationService topicConfigurationService;

    @PostMapping
    public List<Topic> createTopicConfig(@RequestBody List<Topic> topics) {
        return topicConfigurationService.saveTopics(topics);
    }

    @GetMapping
    public List<Topic> getTopicListByKafkaConfigName(@RequestParam String kafkaConfigurationName) {
        return topicConfigurationService.getTopicsByConfigName(kafkaConfigurationName);
    }

}
