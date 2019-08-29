package com.globalrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.globalrelay"})
public class ServiceMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMonitorApplication.class, args);
	}

}
