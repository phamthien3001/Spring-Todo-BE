package com.example.todo.service;

import com.example.todo.dto.request.TodoRequest;
import com.example.todo.dto.response.PageResponse;
import com.example.todo.dto.response.TodoResponse;
import java.util.List;

public interface TodoService {
    PageResponse<TodoResponse> getAllTodos(int page, int size, String sortBy, String sortDir);
    TodoResponse getTodoById(Long id);
    TodoResponse createTodo(TodoRequest todoRequest);
    TodoResponse updateTodo(Long id, TodoRequest todoRequest);
    void deleteTodo(Long id);
}