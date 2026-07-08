package com.example.cepapi;

import com.example.cepapi.entity.CepLog;
import com.example.cepapi.repository.CepLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
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
