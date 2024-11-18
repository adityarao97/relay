package com.example.relayservice.controller;

import com.example.relayservice.entity.KafkaConfiguration;
import com.example.relayservice.entity.response.ApiResponse;
import com.example.relayservice.service.KafkaConfigurationService;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.*;

@RestController
@RequestMapping("/api/kafkaConfiguration")
public class KafkaConfigurationController {

    @Autowired
    private KafkaConfigurationService kafkaConfigurationService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostMapping
    public KafkaConfiguration createKafkaConfiguration(@RequestBody KafkaConfiguration kafkaConfiguration) {
        return kafkaConfigurationService.saveKafkaConfig(kafkaConfiguration);
    }

    @GetMapping
    public KafkaConfiguration getKafkaConfigurationByName(@RequestParam String kafkaConfigurationName) {
        return kafkaConfigurationService.getKafkaConfigByName(kafkaConfigurationName);
    }

    @PostMapping("/startKafkaServer")
    public KafkaConfiguration startKafkaServer(@RequestParam String kafkaConfigurationName) {
        return kafkaConfigurationService.startKafkaServer(kafkaConfigurationName);
    }

    @GetMapping("/checkKafkaStatus")
    public ResponseEntity<ApiResponse> checkKafkaStatus() {
        ApiResponse apiResponse = new ApiResponse();
        Callable<ResponseEntity<ApiResponse>> task = () -> {
            apiResponse.setStatus("200");
            apiResponse.setMessage(kafkaConfigurationService.checkKafkaStatus());
            return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
        };
        try {
            Future<ResponseEntity<ApiResponse>> future = executor.submit(task);
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Kafka is not running", 500), HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/pushMessageToTopic")
    public ResponseEntity<ApiResponse> pushMessageToTopic(@RequestParam String topicName, @RequestBody String message) {
        return kafkaConfigurationService.pushMessageToTopic(topicName, message);
    }

}
