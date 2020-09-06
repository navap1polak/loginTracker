package com.imubit.loginTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages ={"com.imubit.loginTracker"})
public class LoginTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginTrackerApplication.class, args);
	}

}
