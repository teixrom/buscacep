package com.example.cepapi.repository;

import com.example.cepapi.entity.CepLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepLogRepository extends JpaRepository<CepLog, Long> {
}