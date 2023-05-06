package com.ssp.platform.logging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

public interface LogRepository extends JpaRepository<LogEntity, Timestamp> {

}
