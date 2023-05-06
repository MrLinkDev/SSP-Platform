package com.ssp.platform.logging;

import com.ssp.platform.entity.User;
import com.ssp.platform.logging.Service.LogService;
import com.ssp.platform.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

//Формат лога:
//время [роль/username] контроллер: тип_действия параметры: [параметры]
@Component
public class Log {
    public static final String USER_GUEST = "guest";

    public static final String CONTROLLER_FILE = "File";
    public static final String CONTROLLER_PURCHASE = "Purchase";
    public static final String CONTROLLER_QA = "QA";
    public static final String CONTROLLER_SUPPLY = "Supply";
    public static final String CONTROLLER_USER = "User";

    private static final String infoMask = "%s [%s] %s: %s %s";
    private static final String infoUpdateMask = "%s [%s] %s: %s, было: %s, стало: %s";
    private static final String dateMask = "yyyy-MM-dd HH:mm:ss:S";

    private final LogFile logFile;
    private final LogService logService;

    private final LogProperty logProperty;

    @Autowired
    public Log(LogFile logFile, LogService logService, LogProperty logProperty) {
        this.logFile = logFile;
        this.logService = logService;
        this.logProperty = logProperty;
    }

    public void info(String controller, String action, Object ... params) throws IOException {
        if (!logProperty.isEnabled()) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateMask);
        Date infoDate = new Date();

        String line = String.format(infoMask, dateFormat.format(infoDate), USER_GUEST, controller, action, Arrays.toString(params));

        System.out.println(line);

        logFile.put(line, LogFile.GUEST_LOG);

        LogEntity logEntity = new LogEntity();
        logEntity.setDate(Timestamp.from(infoDate.toInstant()));
        logEntity.setUsername(USER_GUEST);
        logEntity.setRole(USER_GUEST);
        logEntity.setActionController(controller);
        logEntity.setActionType(action);
        logEntity.setActionParams(Arrays.toString(params));
        logEntity.setActionSucceed(true);

        logService.put(logEntity);
    }

    public void info(User user, String controller, String action, Object ... params) throws IOException {
        if (!logProperty.isEnabled()) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateMask);
        Date infoDate = new Date();

        String line = String.format(infoMask, dateFormat.format(infoDate), user.getUsername(), controller, action, Arrays.toString(params));

        System.out.println(line);

        logFile.put(line, user.getRole().equals("firm") ? LogFile.FIRM_LOG : LogFile.EMPLOYEE_LOG);

        LogEntity logEntity = new LogEntity();
        logEntity.setDate(Timestamp.from(infoDate.toInstant()));
        logEntity.setUsername(user.getUsername());
        logEntity.setRole(user.getRole());
        logEntity.setActionController(controller);
        logEntity.setActionType(action);
        logEntity.setActionParams(Arrays.toString(params));
        logEntity.setActionSucceed(true);

        logService.put(logEntity);
    }

    public void info(User user, String controller, String action, Object[] was, Object[] became) throws IOException {
        if (!logProperty.isEnabled()) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateMask);
        Date infoDate = new Date();

        String line = String.format(infoUpdateMask, dateFormat.format(infoDate), user.getUsername(), controller, action, Arrays.toString(was), Arrays.toString(became));

        System.out.println(line);

        logFile.put(line, user.getRole().equals("firm") ? LogFile.FIRM_LOG : LogFile.EMPLOYEE_LOG);

        LogEntity logEntity = new LogEntity();
        logEntity.setDate(Timestamp.from(infoDate.toInstant()));
        logEntity.setUsername(user.getUsername());
        logEntity.setRole(user.getRole());
        logEntity.setActionController(controller);
        logEntity.setActionType(action);
        logEntity.setActionParams("было: " + Arrays.toString(was) + ", стало: " + Arrays.toString(became));
        logEntity.setActionSucceed(true);

        logService.put(logEntity);
    }
}
