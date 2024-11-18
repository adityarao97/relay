package com.example.relayservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "kafka_config")
public class KafkaConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "kafka_port", nullable = false, length = 255)
    private int kafkaPort;
    @Column(name = "name")
    private String name;
    @Column(name = "zookeeper_port", nullable = false, length = 255)
    private int zookeeperPort;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Transient
    private List<Topic> topics;
    @Transient
    private List<Consumer> consumers;

    @PrePersist
    protected void onCreate() {
        if(createdAt == null){
            createdAt = Timestamp.from(Instant.now());
        }
        if(updatedAt == null){
            updatedAt = Timestamp.from(Instant.now());
        }
        if(isActive == null){
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.from(Instant.now());
    }
}