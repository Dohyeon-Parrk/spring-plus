package org.example.expert.domain.todo.repository.querydsl.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.*;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.querydsl.TodoRepositoryCustom;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
	public Page<TodoSearchResponse> queryTodosByFilter(Pageable pageable, String keyword, String nickname, LocalDateTime startDate, LocalDateTime endDate){
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;
		QComment comment = QComment.comment;
		QManager manager = QManager.manager;

		BooleanBuilder whereClause = new BooleanBuilder();

		// 제목 키워드
		if(keyword != null && !keyword.isEmpty()){
			whereClause.and(todo.title.containsIgnoreCase(keyword));
		}

		// 닉네임
		if(nickname != null && !nickname.isEmpty()){
			whereClause.and(user.nickname.containsIgnoreCase(nickname));
		}

		// 생성일 범위
		if(startDate != null){
			whereClause.and(todo.createdAt.goe(startDate));
		}

		if(endDate != null){
			whereClause.and(todo.createdAt.lt(endDate));
		}

		List<TodoSearchResponse> result = jpaQueryFactory
			.select(Projections.constructor(TodoSearchResponse.class,
				todo.title,
				manager.countDistinct(),
				comment.count()
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(whereClause)
			.groupBy(todo.id)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = jpaQueryFactory
			.select(todo.count())
			.from(todo)
			.where(whereClause)
			.fetchCount();

		return PageableExecutionUtils.getPage(result, pageable, () -> total);
	}
}
