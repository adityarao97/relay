package com.example.relayservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "consumer")
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "group_id")
    String groupId;
    @Column(name = "is_active")
    boolean isActive = true;
    @Column(name = "fk_topic_id")
    int topicId;
    @Transient
    String topicName;
    @Column(name = "fk_config_id")
    int kafkaConfigId;
    @Column(name = "request_type")
    String requestType;
    @Column(name = "downstream_api")
    String downstreamAPI;
    @Column(name = "delayed_callback")
    Timestamp delayedCallback = null;
    @Column(name = "created_at")
    Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    @Column(name = "updated_at")
    Timestamp updatedAt = new Timestamp(System.currentTimeMillis());;
    @Transient
    String message;
}
