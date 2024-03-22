package com.licenta.bustravel;

import com.licenta.bustravel.errorhandling.ErrorHandler;
import com.licenta.bustravel.errorhandling.MyFilter;
import com.licenta.bustravel.errorhandling.MyFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.*;

@SpringBootApplication
public class BustravelApplication {
	static Logger logger = Logger.getLogger(BustravelApplication.class.getName());
	public static void main(String[] args) {

		SpringApplication.run(BustravelApplication.class, args);
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("C:\\Andrada\\UBB INFO\\Licenta\\Licenta\\BusTravel_Management_System\\bustravel\\bustravel\\logging\\mylogging.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.FINE);
		logger.addHandler(new ConsoleHandler());
		//adding custom handler
		logger.addHandler(new ErrorHandler());
	}

}
