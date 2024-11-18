package com.example.relayservice.service;

import com.example.relayservice.controller.TopicController;
import com.example.relayservice.entity.Consumer;
import com.example.relayservice.entity.KafkaConfiguration;
import com.example.relayservice.entity.Topic;
import com.example.relayservice.entity.response.ApiResponse;
import com.example.relayservice.repository.KafkaConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KafkaConfigurationServiceImpl implements KafkaConfigurationService {
    @Autowired
    private KafkaConfigRepository kafkaConfigRepository;

    @Autowired
    private TopicConfigurationService topicConfigurationService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ConfigurableEnvironment environment;
    private final String kafkaConfigTemplateFilePath = "src/main/resources/templates/KafkaConfigTemplate.yml";

    @Override
    public KafkaConfiguration getKafkaConfigByName(String name) {
        KafkaConfiguration kafkaConfiguration = kafkaConfigRepository.findByName(name);
        if (kafkaConfiguration == null) {
            throw new EntityNotFoundException("given kafka configuration with name " + name + " doesn't exist");
        }
        return kafkaConfiguration;
    }

    @Override
    public KafkaConfiguration saveKafkaConfig(KafkaConfiguration kafkaConfig) {
        return kafkaConfigRepository.save(kafkaConfig);
    }

    @Override
    public KafkaConfiguration startKafkaServer(String kafkaConfigName) {
        KafkaConfiguration kafkaConfiguration = getKafkaConfigByName(kafkaConfigName);
        dockerCompose(kafkaConfigTemplateFilePath, kafkaConfiguration);
        if (!kafkaService.isKafkaRunning()) {
            throw new RuntimeException("Error running kafka");
        }
        //check if any producers exist for this kafkaConfig
        List<Topic> topicList = topicConfigurationService.getTopicsByConfigName(kafkaConfigName);
        if (!topicList.isEmpty()) {
            kafkaConfiguration.setTopics(topicList);
            createTopics(topicList);
//            consumerService.subscribeTopics(kafkaConfiguration.getId());
        }
        return kafkaConfiguration;
    }

    @Override
    public ResponseEntity<ApiResponse> checkKafkaStatus() {
        String response = "Kafka is running\nTopics are : ";
        if (kafkaService.isKafkaRunning()) {
            response += kafkaService.listTopics();
            return new ResponseEntity<>(new ApiResponse("200", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("400", "Kafka is not running"), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ApiResponse> pushMessageToTopic(String topicName, String message) {
        if (!kafkaService.isKafkaRunning()) {
            throw new RuntimeException("Kafka is not running!");
        }
        Topic topic = topicConfigurationService.getTopicByName(topicName);
        List<String> activeTopics = kafkaService.listTopics();
        if(!activeTopics.contains(topic.getName())){
            throw new RuntimeException("Topic: " + topicName + " is not active");
        }
        topicConfigurationService.sendMessageToKafka(topicName, message);
        List<Consumer> consumers = filterUniqueGroupIds(consumerService.getConsumerListByTopicName(topicName));
        for(Consumer consumer: consumers){
            MutablePropertySources propertySources = environment.getPropertySources();
            Properties props = new Properties();
            props.put("config.topic", topicName);
            props.put("config.group", consumer.getGroupId());
            PropertiesPropertySource propertySource = new PropertiesPropertySource("dynamicProps", props);
            propertySources.addFirst(propertySource);
            consumerService.consumeMessage(message);
        }
        return new ResponseEntity<>(new ApiResponse("200", "Success"), HttpStatus.OK);
    }

    private void dockerCompose(String kafkaConfigTemplateFilePath, KafkaConfiguration kafkaConfiguration) {
        commandService.executeCommand("SET KAFKA_PORT=" + kafkaConfiguration.getKafkaPort());
        commandService.executeCommand("SET ZOOKEEPER_PORT=" + kafkaConfiguration.getZookeeperPort());
        MutablePropertySources propertySources = environment.getPropertySources();
        Properties props = new Properties();
        props.put("spring.kafka.bootstrap-servers", "localhost:"+kafkaConfiguration.getKafkaPort());
        PropertiesPropertySource propertySource = new PropertiesPropertySource("dynamicProps", props);
        propertySources.addFirst(propertySource);
        String dockerComposeResponse = commandService.executeCommand("docker-compose -f " + kafkaConfigTemplateFilePath + " up --build -d");
        if (dockerComposeResponse.startsWith("error during connect")) {
            throw new RuntimeException("Docker Daemon/Docker Desktop is not running, ensure it is running and retry!");
        }
    }

    private void createTopics(List<Topic> topics) {
        kafkaService.createMultipleTopics(topics);
        kafkaService.listTopics();
    }

    public List<Consumer> filterUniqueGroupIds(List<Consumer> consumers) {
        Set<String> seenGroupIds = new HashSet<>();  // To track unique groupIds
        return consumers.stream()
                .filter(consumer -> seenGroupIds.add(consumer.getGroupId())) // add returns false if the value already exists
                .collect(Collectors.toList());  // Collect the unique consumers into a new list
    }

}
