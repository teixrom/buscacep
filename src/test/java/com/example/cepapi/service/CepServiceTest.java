package com.example.cepapi.service;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.repository.CepLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private CepLogRepository cepLogRepository;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private CepService cepService;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.setConnectTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        cepService = new CepService("http://mock.api/cep", restTemplateBuilder, cepLogRepository);
    }

    @Test
    void shouldReturnCepInfoWhenApiResponds() {
        String cep = "01001-000";
        String expectedResponse = "{\"cep\":\"01001-000\"}";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(expectedResponse);

        String result = cepService.getCepInfo(cep);

        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldReturnErrorWhenCepNotFound() {
        String cep = "00000-000";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenThrow(mock(HttpClientErrorException.NotFound.class));

        String result = cepService.getCepInfo(cep);

        assertTrue(result.contains("nao encontrado"));
    }

    @Test
    void shouldReturnErrorOnGenericException() {
        String cep = "01001-000";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("timeout"));

        String result = cepService.getCepInfo(cep);

        assertTrue(result.contains("Erro ao processar consulta"));
    }

    @Test
    void shouldPersistLogAfterSuccessfulQuery() {
        String cep = "01001-000";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{}");

        cepService.getCepInfo(cep);

        ArgumentCaptor<CepLog> captor = ArgumentCaptor.forClass(CepLog.class);
        verify(cepLogRepository, times(1)).save(captor.capture());

        CepLog saved = captor.getValue();
        assertEquals("01001000", saved.getCep());
        assertNotNull(saved.getTimestamp());
        assertEquals("{}", saved.getResponse());
    }

    @Test
    void shouldPersistLogEvenWhenApiFails() {
        String cep = "01001-000";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("error"));

        cepService.getCepInfo(cep);

        verify(cepLogRepository, times(1)).save(any(CepLog.class));
    }

    @Test
    void shouldReturnAllLogs() {
        CepLog log1 = new CepLog();
        log1.setId(1L);
        when(cepLogRepository.findAll()).thenReturn(List.of(log1));

        List<CepLog> logs = cepService.getAllLogs();

        assertEquals(1, logs.size());
        assertEquals(1L, logs.get(0).getId());
    }

    @Test
    void shouldSanitizeCepRemovingNonDigits() {
        String cep = "abc01001-000xyz";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{}");

        cepService.getCepInfo(cep);

        verify(restTemplate).getForObject("http://mock.api/cep?cep=01001000", String.class);
    }
}