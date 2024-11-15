package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /*
        Comment 엔티티 fetchType = LAZY 로 인해 발생하는 문제 -> JPQL Fetch Join 사용
        - 연관된 엔티티를 사용할 때마다 해당 엔티티를 개별적으로 조회하기 때문에 N+1 번 쿼리 실행됨.
	*/
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")
    List<Comment> findAllByTodoIdWithUser(@Param("todoId") Long todoId);
}
