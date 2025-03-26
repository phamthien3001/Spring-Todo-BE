package com.example.todo.controller;

import com.example.todo.dto.request.TodoRequest;
import com.example.todo.dto.response.PageResponse;
import com.example.todo.dto.response.TodoResponse;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<TodoResponse>> getAllTodos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok((PageResponse<TodoResponse>) todoService.getAllTodos(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest todoRequest) {
        return new ResponseEntity<>(todoService.createTodo(todoRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest todoRequest) {
        return ResponseEntity.ok(todoService.updateTodo(id, todoRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}