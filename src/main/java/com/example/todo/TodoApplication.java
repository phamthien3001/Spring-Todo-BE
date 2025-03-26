// src/main/java/com/example/todo/TodoApplication.java
package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.example.todo.model.Role;
import com.example.todo.model.ERole;
import com.example.todo.repository.RoleRepository;

@SpringBootApplication
public class TodoApplication {
	private final RoleRepository roleRepository;

	public TodoApplication(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initRoles() {
		for (ERole role : ERole.values()) {
			if (!roleRepository.existsByName(role)) {
				roleRepository.save(new Role(role));
			}
		}
	}
}