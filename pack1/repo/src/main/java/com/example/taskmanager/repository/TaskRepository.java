package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusIn(List<TaskStatus> statuses);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.category = :category AND t.status != 'CANCELLED'")
    List<Task> findByCategory(@Param("category") String category);

    @Query("SELECT t FROM Task t WHERE t.tags LIKE %:tag% AND t.status != 'CANCELLED'")
    List<Task> findByTag(@Param("tag") String tag);

    // Bug: This query has incorrect JPQL syntax
    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' OR t.status = 'IN_PROGRESS' ORDER BY t.priority ASC")
    List<Task> findActiveTasks();

    long countByStatus(TaskStatus status);
}