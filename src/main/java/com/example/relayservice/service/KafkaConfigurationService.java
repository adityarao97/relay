package com.example.relayservice.service;

import com.example.relayservice.entity.KafkaConfiguration;
import com.example.relayservice.entity.response.ApiResponse;
import jakarta.xml.ws.Response;
import org.springframework.http.ResponseEntity;

public interface KafkaConfigurationService {
    public KafkaConfiguration getKafkaConfigByName(String kafkaConfigName);
    public KafkaConfiguration saveKafkaConfig(KafkaConfiguration kafkaConfig);
    public KafkaConfiguration startKafkaServer(String kafkaConfigName);
    public ResponseEntity<ApiResponse> checkKafkaStatus();
    public ResponseEntity<ApiResponse> pushMessageToTopic(String topicName, String message);
}
