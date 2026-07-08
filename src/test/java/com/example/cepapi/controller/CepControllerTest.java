package com.example.cepapi.controller;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.service.CepService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.user.name=admin",
    "spring.security.user.password=admin123"
})
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CepService cepService;

    @Test
    void shouldReturnCepInfo() throws Exception {
        when(cepService.getCepInfo("01001-000")).thenReturn("{\"cep\":\"01001-000\"}");

        mockMvc.perform(get("/cep?cep=01001-000"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"cep\":\"01001-000\"}"));
    }

    @Test
    void shouldRejectInvalidCep() throws Exception {
        mockMvc.perform(get("/cep?cep=abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCepWithWrongLength() throws Exception {
        mockMvc.perform(get("/cep?cep=123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnLogs() throws Exception {
        CepLog log = new CepLog();
        log.setId(1L);
        log.setCep("00000-000");
        log.setTimestamp(LocalDateTime.now());
        log.setResponse("{}");

        when(cepService.getAllLogs()).thenReturn(List.of(log));

        mockMvc.perform(get("/logs").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cep").value("00000-000"));
    }

    @Test
    void shouldRejectLogsWithoutAuth() throws Exception {
        mockMvc.perform(get("/logs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectLogsWithWrongPassword() throws Exception {
        mockMvc.perform(get("/logs").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }
}