package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Task.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getActiveTasks() {
        // Bug: This calls a method with incorrect JPQL
        return taskRepository.findActiveTasks();
    }

    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }

    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }

    public Task createTask(Task task) {
        // Bug: No validation of title length before save
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        task.setCategory(taskDetails.getCategory());
        task.setTags(taskDetails.getTags());

        // Bug: Not handling completedAt properly when status changes to COMPLETED
        return taskRepository.save(task);
    }

    public Task patchTask(Long id, Task patchData) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (patchData.getTitle() != null) {
            task.setTitle(patchData.getTitle());
        }
        if (patchData.getDescription() != null) {
            task.setDescription(patchData.getDescription());
        }
        if (patchData.getStatus() != null) {
            task.setStatus(patchData.getStatus());
            // Bug: completedAt not set when completing task
        }
        if (patchData.getPriority() != null) {
            task.setPriority(patchData.getPriority());
        }
        if (patchData.getDueDate() != null) {
            task.setDueDate(patchData.getDueDate());
        }
        if (patchData.getCategory() != null) {
            task.setCategory(patchData.getCategory());
        }
        if (patchData.getTags() != null) {
            task.setTags(patchData.getTags());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setStatus(TaskStatus.COMPLETED);
        // Bug: completedAt should be set here but isn't
        return taskRepository.save(task);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void markOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        // Bug: This method doesn't actually do anything with the overdue tasks
        // Should log or notify about overdue tasks
    }

    public long countByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
}