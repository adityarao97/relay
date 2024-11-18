package com.example.relayservice.service;

import com.example.relayservice.entity.KafkaConfiguration;
import com.example.relayservice.entity.Topic;
import com.example.relayservice.repository.KafkaConfigRepository;
import com.example.relayservice.repository.TopicRepository;
import com.example.relayservice.specification.TopicSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicConfigurationServiceImpl implements TopicConfigurationService {

    @Autowired
    private KafkaConfigRepository kafkaConfigRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public List<Topic> getTopicsByConfigName(String kafkaConfigName) {
        KafkaConfiguration kafkaConfiguration = kafkaConfigRepository.findByName(kafkaConfigName);
        if (kafkaConfiguration == null) {
            throw new EntityNotFoundException("Given kafka configuration doesn't exist");
        }
        return topicRepository.findAll(TopicSpecification.hasConfigId(kafkaConfiguration.getId()));
    }

    @Override
    public List<Topic> getTopicsByConfigId(int kafkaConfigId) {
        return topicRepository.findAll(TopicSpecification.hasConfigId(kafkaConfigId));
    }

    @Override
    public Topic getTopicByName(String topicName) {
        Optional<Topic> optionalTopic = topicRepository.findOne(TopicSpecification.hasName(topicName));
        if(optionalTopic.isEmpty()){
            throw new RuntimeException("topic not found");
        }
        return optionalTopic.get();
    }

    @Override
    public List<Topic> saveTopics(List<Topic> topics) {
        return topicRepository.saveAll(topics);
    }

    @Override
    public void sendMessageToKafka(String topicName, String message) {
        kafkaTemplate.send(topicName, message);
        System.out.println("Message sent to Kafka topic: " + message);
    }
}
