package com.example.cepapi.service;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.repository.CepLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CepService {

    @Value("${cep.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final CepLogRepository cepLogRepository;

    public CepService(CepLogRepository cepLogRepository) {
        this.cepLogRepository = cepLogRepository;
    }

    public String getCepInfo(String cep) {
        String urlFinal = apiUrl + "?cep=" + cep;
        String responseToReturn;

        try {

            responseToReturn = restTemplate.getForObject(urlFinal, String.class);
        } catch (HttpClientErrorException.NotFound e) {

            responseToReturn = "{\"error\": \"CEP não encontrado\"}";
        } catch (Exception e) {

            responseToReturn = "{\"error\": \"Erro ao processar consulta: " + e.getMessage() + "\"}";
        }


        saveLog(cep, responseToReturn);

        return responseToReturn;
    }

    // Método privado auxiliar para manter o código limpo
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
