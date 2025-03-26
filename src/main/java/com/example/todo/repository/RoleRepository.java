// src/main/java/com/example/todo/repository/RoleRepository.java
package com.example.todo.repository;

import com.example.todo.model.ERole;
import com.example.todo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name); // Thêm phương thức này
}