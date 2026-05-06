package com.example.cepapi.repository;

import com.example.cepapi.entity.CepLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest; // IMPORTANTE
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest // <--- USE ESTA ANOTAÇÃO. REMOVA O @SpringBootTest
public class CepLogRepositoryTest {

    @Autowired
    private CepLogRepository cepLogRepository;

    @Test
    public void testSaveLog() {
        CepLog logEntry = new CepLog();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setCep("12345-678");
        logEntry.setResponse("{}");

        cepLogRepository.save(logEntry);

        List<CepLog> logs = cepLogRepository.findAll();
        assertEquals(1, logs.size());
    }
}
