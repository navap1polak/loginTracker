package com.imubit.loginTracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Slf4j
@SpringBootApplication
@EntityScan(basePackages ={"com.imubit.loginTracker"})
public class LoginTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginTrackerApplication.class, args);
	}

}
