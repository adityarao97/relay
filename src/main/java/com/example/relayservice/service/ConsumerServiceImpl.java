package com.example.relayservice.service;

import com.example.relayservice.entity.Consumer;
import com.example.relayservice.entity.Topic;
import com.example.relayservice.repository.ConsumerRepository;
import com.example.relayservice.specification.ConsumerSpecification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private TopicConfigurationService topicConfigurationService;

    @Autowired
    private Environment environment;


    private final ApiService apiService;

    public ConsumerServiceImpl(ApiService apiService) {
        this.apiService = apiService;
    }


//    @Override
//    public void subscribeTopics(int kafkaConfigId) {
////        List<Topic> topics = topicConfigurationService.getTopicsByConfigId(kafkaConfigId);
//        List<Consumer> consumers = getConsumerListByConfigId(kafkaConfigId);
//        if(consumers.isEmpty()){
//            return;
//        }
//        for(Consumer consumer: consumers){
//            consumer.setTopicName(setTopicNameByTopicId(consumer.getTopicId(), topics));
//        }
//        loadAndStartConsumers(consumers);
//    }

    private String setTopicNameByTopicId(int topicId, List<Topic> topicList){
        for(Topic topic: topicList){
            if(topic.getId()==topicId){
                return topic.getName();
            }
        }
        return "";
    }

    @Override
    @KafkaListener(topics = "${config.topic}", groupId = "${config.group}")
    public void consumeMessage(String message) {
        String groupId = environment.getProperty("config.group");
        List<Consumer> consumers = consumerRepository.findAll(ConsumerSpecification.hasGroupId(groupId));
        for(Consumer consumer : consumers){
            apiService.callApiWithMessage(consumer.getDownstreamAPI(), consumer.getRequestType(), message);
        }
        System.out.println(groupId + " consumed the message : " + message);
    }

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

    @Override
    public List<Consumer> createConsumer(List<Consumer> consumer) {
        return consumerRepository.saveAll(consumer);
    }

    @Override
    public List<Consumer> getConsumerListByConfigId(int configId) {
        return consumerRepository.findAll(ConsumerSpecification.hasKafkaConfigId(configId));
    }

    @Override
    public List<Consumer> getConsumerListByTopicName(String topicName) {
        Topic topic = topicConfigurationService.getTopicByName(topicName);
        return consumerRepository.findAll(ConsumerSpecification.hasTopicId(topic.getId()));
    }

    // Assuming kafkaConsumerRepository is injected to get the consumer list from DB
    public void loadAndStartConsumers(List<Consumer> consumers) {
        // For each consumer, create and start a Kafka listener dynamically
        for (Consumer consumer : consumers) {
            startListeningForMessages(consumer);  // Dynamically start a consumer based on each entry
        }
    }

    private void startListeningForMessages(Consumer consumer) {
        // Kafka consumer configuration
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");  // Kafka broker address
        properties.put("group.id", consumer.getGroupId());  // Set the consumer group ID dynamically
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("enable.auto.commit", "true");  // Enable auto-commit of offsets
        properties.put("auto.commit.interval.ms", "1000");  // Set auto-commit interval

        // Create a new KafkaConsumer using the properties
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        // Subscribe the consumer to the topic
        kafkaConsumer.subscribe(List.of(consumer.getTopicName()));  // The consumer listens to the specified topic
        MessageListener<String, String> listener = createMessageListener(consumer.getName(), consumer.getGroupId());
        ContainerProperties containerProperties = new ContainerProperties(consumer.getTopicName());
        containerProperties.setGroupId(consumer.getGroupId());
        containerProperties.setKafkaConsumerProperties(properties);
        containerProperties.setMessageListener(listener);
        // Set up a listener container for this consumer
        MessageListenerContainer listenerContainer = new ConcurrentMessageListenerContainer<>(
                consumerFactory, containerProperties);  // Link the consumer to the topic
        listenerContainer.start();  // Start consuming the messages
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));  // Poll every 1000ms
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Consumed message: " + record.value());
            }
            kafkaConsumer.commitSync();  // Commit the offsets
        }
    }

}

