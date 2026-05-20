package com.example.memo.repository;

import com.example.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    
    List<Memo> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Memo> findByCategoryId(Long categoryId);
    
    List<Memo> findByIsCompleted(Boolean isCompleted);
    
    @Query("SELECT m FROM Memo m WHERE m.userId = :userId AND m.isPinned = true ORDER BY m.createdAt DESC")
    List<Memo> findPinnedMemos(Long userId);
    
    @Query("SELECT m FROM Memo m WHERE m.userId = :userId AND m.isCompleted = false ORDER BY m.dueDate ASC")
    List<Memo> findPendingMemos(Long userId);
    
    @Query("SELECT COUNT(m) FROM Memo m WHERE m.userId = :userId AND m.isCompleted = true")
    Long countCompletedMemos(Long userId);
    
    @Query("SELECT COUNT(m) FROM Memo m WHERE m.userId = :userId")
    Long countTotalMemos(Long userId);
}