package com.example.memo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemoDTO {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private Boolean isCompleted;
    private Boolean isPinned;
    private LocalDateTime reminderTime;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}