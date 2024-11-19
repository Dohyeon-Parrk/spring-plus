package org.example.expert.domain.todo.repository.querydsl.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.*;

import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.querydsl.TodoRepositoryCustom;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId){
		Todo result = jpaQueryFactory
			.selectFrom(todo)
			.leftJoin(todo.user, user).fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public Page<Todo> queryTodosByFilter(Pageable pageable, String keyword, String nickname, LocalDateTime startDate, LocalDateTime endDate){
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;

		BooleanBuilder whereClause = new BooleanBuilder();
		if(keyword != null && !keyword.isEmpty()){
			whereClause.and(todo.title.containsIgnoreCase(keyword));
		}

		if(nickname != null && !nickname.isEmpty()){
			whereClause.and(user.nickname.containsIgnoreCase(nickname));
		}

		if(startDate != null){
			whereClause.and(todo.createdAt.goe(startDate));
		}

		if(endDate != null){
			whereClause.and(todo.createdAt.lt(endDate));
		}

		List<Todo> todos = jpaQueryFactory
			.selectFrom(todo)
			.leftJoin(todo.user, user)
			.where(whereClause)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = jpaQueryFactory
			.selectFrom(todo)
			.leftJoin(todo.user, user)
			.where(whereClause)
			.fetchCount();

		return PageableExecutionUtils.getPage(todos, pageable, () -> total);
	}
}
