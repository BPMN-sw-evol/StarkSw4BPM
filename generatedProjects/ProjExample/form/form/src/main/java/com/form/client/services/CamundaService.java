package com.form.client.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CamundaService {

    private final RestTemplate restTemplate;

    public CamundaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void iniciarProcesoFormulario(Map<String, Object> variables) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = "http://localhost:8080/engine-rest/process-definition/key/Process_19oqlmt/start";

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("Respuesta de Camunda: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
    }
}

