package com.example.cepapi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.cepapi.service.CepService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CepServiceTest {

    @LocalServerPort
    private int port; // Porta aleatória do seu app (opcional aqui)

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private CepService cepService;

    @BeforeEach
    public void setUp() {
        // 1. Limpa todos os stubs e histórico de requisições anteriores
        wireMockServer.resetAll();

        // 2. Configura o novo stub
        wireMockServer.stubFor(get(urlPathEqualTo("/cep"))
                // Usamos matching() ou equalTo() para garantir que o parâmetro seja validado
                .withQueryParam("cep", equalTo("12345-678"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        // Use o formato exato que o seu assertEquals espera
                        .withBody("{\"cep\":\"12345-678\",\"logradouro\":\"Rua Exemplo\",\"bairro\":\"Bairro Exemplo\",\"cidade\":\"Cidade Exemplo\",\"uf\":\"UF\"}")));
    }




    @Test
    public void testGetCepInfo() {
        String result = cepService.getCepInfo("12345-678");
        assertEquals("{\"cep\":\"12345-678\",\"logradouro\":\"Rua Exemplo\",\"bairro\":\"Bairro Exemplo\",\"cidade\":\"Cidade Exemplo\",\"uf\":\"UF\"}", result);
    }
}
