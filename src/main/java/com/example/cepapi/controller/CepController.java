package com.example.cepapi.controller;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;

@RestController
public class CepController {

    @Autowired
    private CepService cepService;

    @GetMapping("/cep") // Mapeia localhost:8081/cep
    public ResponseEntity<String> getCepInfo(@RequestParam String cep) {
        return ResponseEntity.ok(cepService.getCepInfo(cep));
    }

    @GetMapping("/logs") // Nova rota para buscar logs
    public ResponseEntity<List<CepLog>> getAllLogs() {
        List<CepLog> logs = cepService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}
