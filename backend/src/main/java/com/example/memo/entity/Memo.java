package com.example.memo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "memos")
@Data
public class Memo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "is_pinned")
    private Boolean isPinned = false;
    
    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isCompleted == null) isCompleted = false;
        if (isPinned == null) isPinned = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}