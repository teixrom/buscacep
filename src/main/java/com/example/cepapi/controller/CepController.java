package com.example.cepapi.controller;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.service.CepService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Validated
@RestController
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/cep")
    public ResponseEntity<String> getCepInfo(
            @RequestParam
            @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve conter 8 dígitos (formato: 12345-678 ou 12345678)")
            String cep) {
        return ResponseEntity.ok(cepService.getCepInfo(cep));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<CepLog>> getAllLogs() {
        List<CepLog> logs = cepService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}