package com.example.cepapi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.security.user.name=admin",
    "spring.security.user.password=admin123",
    "cep.api.url=http://localhost:9090/cep",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class CepApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9090);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnCepInfo() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/cep"))
                .withQueryParam("cep", WireMock.equalTo("01001000"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"cep\":\"01001-000\",\"logradouro\":\"Praca da Se\"}")));

        String url = "http://localhost:" + port + "/cep?cep=01001-000";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("01001-000");
    }

    @Test
    void shouldReturn404WhenCepNotFound() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/cep"))
                .withQueryParam("cep", WireMock.equalTo("00000000"))
                .willReturn(WireMock.aResponse().withStatus(404)));

        String url = "http://localhost:" + port + "/cep?cep=00000-000";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("nao encontrado");
    }

    @Test
    void shouldRejectInvalidCepFormat() {
        String url = "http://localhost:" + port + "/cep?cep=abc";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnLogsWithAuth() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/cep"))
                .withQueryParam("cep", WireMock.equalTo("01001000"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"cep\":\"01001-000\"}")));

        restTemplate.getForEntity("http://localhost:" + port + "/cep?cep=01001-000", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/logs",
                HttpMethod.GET,
                entity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("01001000");
    }

    @Test
    void shouldRejectLogsWithoutAuth() {
        String url = "http://localhost:" + port + "/logs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectLogsWithWrongPassword() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "wrong");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/logs",
                HttpMethod.GET,
                entity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void healthEndpointShouldBePublic() {
        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}