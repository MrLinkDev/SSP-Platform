package com.ssp.platform;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class PlatformApplication {

	private static final Logger logger = Logger.getLogger(PlatformApplication.class);

    public static void main(String[] args) throws IOException {
        //Загрузка настроек логера из log4j.properties
		Properties props = new Properties();
		props.load(new FileInputStream("./src/main/resources/log4j.properties"));
		PropertyConfigurator.configure(props);

		logger.info("Starting application..");

		try {
			SpringApplication.run(PlatformApplication.class, args);
        } catch (Exception ex){
			String message = ex.getMessage() == null?"Application failed... StackTrace:\n":ex.getMessage();
			logger.error( message + Arrays.toString(ex.getStackTrace()), ex);
		}
    }
	//System.out.println("123");  для копирования
}
