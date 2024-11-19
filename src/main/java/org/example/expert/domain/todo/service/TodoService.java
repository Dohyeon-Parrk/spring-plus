package org.example.expert.domain.todo.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, String modifiedAt) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // modifiedAt 파라미터를 기간 시작일, 기간 마지막일로 파싱
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if(modifiedAt != null && !modifiedAt.isEmpty()) {
            String[] dates = modifiedAt.split(",");
            if(dates.length == 2){
                startDate = LocalDateTime.parse(dates[0].trim());
                endDate = LocalDateTime.parse(dates[1].trim());
            }
        }

        Page<Todo> todos;

        if(weather != null && startDate != null && endDate != null) {
            todos = todoRepository.findByWeatherAndModifiedAtBetween(pageable, weather, startDate, endDate);
        } else if(weather != null) {
            todos = todoRepository.findByWeather(pageable, weather);
        } else if(startDate != null && endDate != null) {
            todos = todoRepository.findByModifiedAtBetween(pageable, startDate, endDate);
        } else {
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }


        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Page<TodoSearchResponse> searchTodos(String keyword, String nickname, String startDate, String endDate,
        int page, int size) {
        Pageable pageable = PageRequest.of(
            page - 1,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        LocalDateTime parsingStartDate = null;
        LocalDateTime parsingEndDate = null;

        if (startDate != null && !startDate.trim().isEmpty()){
            parsingStartDate = LocalDateTime.parse(startDate.trim());
        }

        if (endDate != null && !endDate.trim().isEmpty()){
            parsingEndDate = LocalDateTime.parse(endDate.trim());
        }

        return todoRepository.queryTodosByFilter(
            pageable,
            keyword,
            nickname,
            parsingStartDate,
            parsingEndDate
        );
    }
}
