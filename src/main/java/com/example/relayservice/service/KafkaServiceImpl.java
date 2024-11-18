package com.example.relayservice.service;

import com.example.relayservice.entity.Topic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private AdminClient adminClient;

    @Override
    public List<String> listTopics() {
        ListTopicsResult topics = adminClient.listTopics();
//        System.out.println("consumer groups are : " + adminClient.describeConsumerGroups(Arrays.asList("consumer1")).all());
        try {
            return topics.names().get().stream().toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isKafkaRunning() {
        listTopics();
        return true;
    }

    @Override
    public void createMultipleTopics(List<Topic> topics) {
        List<String> existingTopics = listTopics();

        List<Topic> testNewTopics = topics.stream().filter(topic -> !existingTopics.contains(topic.getName())).toList();

        // Filter out the topics that already exist
        List<NewTopic> newTopics = testNewTopics.stream()
                .map(topic -> new NewTopic(topic.getName(), 2, (short) 1))  // Create new topic with 2 partitions, 1 replication factor
                .collect(Collectors.toList());

        // Only create topics if there are new ones to create
        if (!newTopics.isEmpty()) {
            CreateTopicsResult result = adminClient.createTopics(newTopics);
            try {
                result.all().get(); // Wait for the topic creation to complete
                System.out.println("Topics created successfully.");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to create topics", e);
            }
        } else {
            System.out.println("All topics already exist, no new topics to create.");
        }
    }
}
