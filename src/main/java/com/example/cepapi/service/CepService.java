package com.example.cepapi.service;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.repository.CepLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CepService {

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final CepLogRepository cepLogRepository;

    public CepService(
            @Value("${cep.api.url}") String apiUrl,
            RestTemplateBuilder restTemplateBuilder,
            CepLogRepository cepLogRepository) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        this.cepLogRepository = cepLogRepository;
    }

    public String getCepInfo(String cep) {
        String sanitizedCep = cep.replaceAll("[^0-9]", "");
        String urlFinal = apiUrl + "?cep=" + sanitizedCep;
        String responseToReturn;

        try {
            responseToReturn = restTemplate.getForObject(urlFinal, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            responseToReturn = "{\"error\": \"CEP nao encontrado\"}";
        } catch (Exception e) {
            responseToReturn = "{\"error\": \"Erro ao processar consulta\"}";
        }

        saveLog(sanitizedCep, responseToReturn);
        return responseToReturn;
    }

    private void saveLog(String cep, String response) {
        CepLog logEntry = new CepLog();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setCep(cep);
        logEntry.setResponse(response);
        cepLogRepository.save(logEntry);
    }

    public List<CepLog> getAllLogs() {
        return cepLogRepository.findAll();
    }
}