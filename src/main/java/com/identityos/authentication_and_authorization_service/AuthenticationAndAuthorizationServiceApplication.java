package com.identityos.authentication_and_authorization_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class
})

public class AuthenticationAndAuthorizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationAndAuthorizationServiceApplication.class, args);
	}

}
