package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.querydsl.TodoRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("select t "
        + "from Todo t "
        + "where t.weather = :weather and t.modifiedAt "
        + "between :startDate and :endDate "
        + "order by t.modifiedAt desc")
    Page<Todo> findByWeatherAndModifiedAtBetween(Pageable pageable,
        @Param("weather") String weather,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("select t "
        + "from Todo t "
        + "where t.weather = :weather "
        + "order by t.modifiedAt desc")
    Page<Todo> findByWeather(Pageable pageable, @Param("weather") String weather);

    @Query("select t "
        + "from Todo t "
        + "where t.modifiedAt "
        + "between :startDate and :endDate "
        + "order by t.modifiedAt desc")
    Page<Todo> findByModifiedAtBetween(Pageable pageable,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("select t "
        + "from Todo t "
        + "order by t.modifiedAt desc")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

}
