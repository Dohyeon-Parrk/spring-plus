package org.example.expert.domain.todo.repository.querydsl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {

	Optional<Todo> findByIdWithUser(Long todoId);

	Page<TodoSearchResponse> queryTodosByFilter(
		Pageable pageable,
		String keyword,
		String nickname,
		LocalDateTime startDate,
		LocalDateTime endDate
	);

}
