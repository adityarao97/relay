package com.example.relayservice.specification;

import com.example.relayservice.entity.Consumer;
import org.springframework.data.jpa.domain.Specification;

public class ConsumerSpecification {

    public static Specification<Consumer> hasKafkaConfigId(int kafkaConfigId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("kafkaConfigId"), kafkaConfigId);
        };
    }
    public static Specification<Consumer> hasTopicId(int topicId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("topicId"), topicId);
        };
    }

    public static Specification<Consumer> hasGroupId(String groupId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("groupId"), groupId);
        };
    }
}
