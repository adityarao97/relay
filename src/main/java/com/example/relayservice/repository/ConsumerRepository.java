package com.example.relayservice.repository;

import com.example.relayservice.entity.Consumer;
import com.example.relayservice.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.module.Configuration;

public interface ConsumerRepository extends JpaRepository<Consumer, Integer>, JpaSpecificationExecutor<Consumer> {
}
