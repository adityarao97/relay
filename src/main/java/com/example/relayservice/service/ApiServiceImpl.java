package com.example.relayservice.service;

import com.example.relayservice.entity.MethodType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiServiceImpl implements ApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void callApiWithMessage(String url, String methodType, String message) {
        try {
            if (!MethodType.isMatch(methodType)) {
                throw new RuntimeException("wrong method type provided");
            }
            MethodType methodTypeVal = MethodType.getEnumFromString(methodType);
            assert methodTypeVal != null;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> httpEntity = new HttpEntity<>(message, headers);
            String response = "";
            if (methodTypeVal.equals(MethodType.GET)) {
                response = restTemplate.getForObject(url, String.class);
            } else if (methodTypeVal.equals(MethodType.POST)) {
                response = restTemplate.postForObject(url, httpEntity, String.class);
            } else if (methodTypeVal.equals(MethodType.PUT)) {
                restTemplate.put(url, message, String.class);
            } else if (methodTypeVal.equals(MethodType.PATCH)) {
                response = restTemplate.patchForObject(url, httpEntity, String.class);
            } else {
                restTemplate.delete(url);
            }
            System.out.println("API response: " + response);
        } catch (Exception e) {
            System.out.println("Error calling API: " + e.getMessage());
        }
    }
}
