package com.example.relayservice.repository;

import com.example.relayservice.entity.KafkaConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KafkaConfigRepository extends JpaRepository<KafkaConfiguration, Integer> {
    KafkaConfiguration findByName(String name);
    KafkaConfiguration findById(int id);
}