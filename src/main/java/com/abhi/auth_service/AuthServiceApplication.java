package com.abhi.auth_service;

import com.abhi.auth_service.config.RabbitMQConfig;
import com.abhi.auth_service.enums.RoleName;
import com.abhi.auth_service.model.Role;
import com.abhi.auth_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.count() == 0) {
				Arrays.stream(RoleName.values())
						.map(roleName -> {
							Role role = new Role();
							role.setName(roleName);
							return role;
						})
						.forEach(roleRepository::save);
			}
		};
	}

}
