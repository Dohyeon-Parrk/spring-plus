package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")

    @Query("select todo "
        + "from Todo todo "
        + "where todo.weather = :weather and todo.modifiedAt "
        + "between :startDate and :endDate "
        + "order by todo.modifiedAt desc")
    Page<Todo> findByWeatherAndModifiedAtBetween(Pageable pageable,
        @Param("weather") String weather,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("select todo "
        + "from Todo todo "
        + "where todo.weather = :weather "
        + "order by todo.modifiedAt desc")
    Page<Todo> findByWeather(Pageable pageable, @Param("weather") String weather);

    @Query("select todo "
        + "from Todo todo "
        + "where todo.modifiedAt "
        + "between :startDate and :endDate "
        + "order by todo.modifiedAt desc")
    Page<Todo> findByModifiedAtBetween(Pageable pageable,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("select todo from Todo todo order by todo.modifiedAt desc")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

}
