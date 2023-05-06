package com.ssp.platform.logging.Service;

import com.ssp.platform.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void put(LogEntity logEntity) {
        logRepository.save(logEntity);
    }
}
