package com.ssp.platform.logging;

import com.ssp.platform.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LogFile {
    public static final String FIRM_LOG = "firm";
    public static final String EMPLOYEE_LOG = "employee";
    public static final String GUEST_LOG = "guest";

    private static final String LOG_TYPE = "log";
    private static final String BACKUP_TYPE = "backup";

    private static final String fileDateMask = "yyyyMMdd";
    private static final String fileNameMask = "log-%s-%s.%s";

    private LogProperty logProperty;
    private final Path logStorageLocation;
    private Path logLocation;
    private Path backupLocation;

    @Autowired
    public LogFile(LogProperty logProperty) throws IOException {
        this.logProperty = logProperty;

        String directory = logProperty.getDirectory();
        if(directory.contains(":")) logStorageLocation = Paths.get(directory);
        else logStorageLocation = Paths.get(directory).toAbsolutePath().normalize();
        if (!Files.exists(logStorageLocation)) {
            Files.createDirectory(logStorageLocation);
        }
    }

    public void put(String line, String role) throws IOException {
        line += "\n";

        SimpleDateFormat dateFormat = new SimpleDateFormat(fileDateMask);
        String fileName = String.format(fileNameMask, dateFormat.format(new Date()), role, LOG_TYPE);
        String backupFileName = String.format(fileNameMask, dateFormat.format(new Date()), role, BACKUP_TYPE);

        String logPath = logProperty.getDirectory() + "\\" + fileName;
        String backupPath = logProperty.getDirectory() + "\\" + backupFileName;
        if(logPath.contains(":")) {
            logLocation = Paths.get(logPath);
            backupLocation = Paths.get(backupPath);
        } else {
            logLocation = Paths.get(logPath).toAbsolutePath().normalize();
            backupLocation = Paths.get(backupPath).toAbsolutePath().normalize();
        }

        if (!Files.exists(logLocation)){
            Files.createFile(logLocation);
        }

        Files.copy(logLocation, backupLocation, StandardCopyOption.REPLACE_EXISTING);
        Files.write(logLocation, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }
}
