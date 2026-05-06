package com.example.cepapi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CepControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WireMockServer wireMockServer; // Injetando o Bean que criamos na Config

    @BeforeEach
    public void setup() {
        // Garante que o WireMock saiba responder ao CEP do teste
        wireMockServer.stubFor(get(urlPathEqualTo("/cep"))
                .withQueryParam("cep", equalTo("01001-000"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"cep\":\"01001-000\",\"logradouro\":\"Praça da Sé\"}")));
    }

    @Test
    public void testGetCepEndpoint() {
        String url = "http://localhost:" + port + "/cep?cep=01001-000";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Se falhar aqui, o erro 500 aparecerá no console com o motivo real (causado pelo Service)
        assertEquals(HttpStatus.OK, response.getStatusCode(), "O Service falhou em processar a requisição.");
        assertTrue(response.getBody().contains("01001-000"));
    }
}
