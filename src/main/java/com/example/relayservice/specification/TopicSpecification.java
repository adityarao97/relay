package com.example.relayservice.specification;

import com.example.relayservice.entity.Topic;
import org.springframework.data.jpa.domain.Specification;

public class TopicSpecification {

    public static Specification<Topic> hasConfigId(int configId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("kafkaConfigId"), configId);
        };
    }

    public static Specification<Topic> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }
}
