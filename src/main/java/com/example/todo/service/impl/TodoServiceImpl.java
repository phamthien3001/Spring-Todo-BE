package com.example.todo.service.impl;

import com.example.todo.dto.request.TodoRequest;
import com.example.todo.dto.response.PageResponse;
import com.example.todo.dto.response.TodoResponse;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponse<TodoResponse> getAllTodos(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Todo> todos = todoRepository.findAll(pageable);

        List<TodoResponse> content = todos.getContent().stream()
                .map(todo -> modelMapper.map(todo, TodoResponse.class))
                .collect(Collectors.toList());

        return PageResponse.<TodoResponse>builder()
                .content(content)
                .pageNo(todos.getNumber())
                .pageSize(todos.getSize())
                .totalElements(todos.getTotalElements())
                .totalPages(todos.getTotalPages())
                .last(todos.isLast())
                .build();
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return modelMapper.map(todo, TodoResponse.class);
    }

    @Override
    public TodoResponse createTodo(TodoRequest todoRequest) {
        Todo todo = modelMapper.map(todoRequest, Todo.class);
        Todo savedTodo = todoRepository.save(todo);
        return modelMapper.map(savedTodo, TodoResponse.class);
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest todoRequest) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        modelMapper.map(todoRequest, existingTodo);
        Todo updatedTodo = todoRepository.save(existingTodo);
        return modelMapper.map(updatedTodo, TodoResponse.class);
    }

    @Override
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }
}