package com.example.memo.service;

import com.example.memo.entity.Memo;
import com.example.memo.repository.MemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemoService {
    
    @Autowired
    private MemoRepository memoRepository;
    
    public List<Memo> getAllMemos(Long userId) {
        return memoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Optional<Memo> getMemoById(Long id) {
        return memoRepository.findById(id);
    }
    
    public Memo createMemo(Memo memo) {
        return memoRepository.save(memo);
    }
    
    public Memo updateMemo(Long id, Memo memoDetails) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));
        memo.setTitle(memoDetails.getTitle());
        memo.setContent(memoDetails.getContent());
        memo.setCategoryId(memoDetails.getCategoryId());
        memo.setIsCompleted(memoDetails.getIsCompleted());
        memo.setIsPinned(memoDetails.getIsPinned());
        memo.setReminderTime(memoDetails.getReminderTime());
        memo.setDueDate(memoDetails.getDueDate());
        return memoRepository.save(memo);
    }
    
    public void deleteMemo(Long id) {
        memoRepository.deleteById(id);
    }
    
    public Memo toggleComplete(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));
        memo.setIsCompleted(!memo.getIsCompleted());
        return memoRepository.save(memo);
    }
    
    public Memo togglePin(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));
        memo.setIsPinned(!memo.getIsPinned());
        return memoRepository.save(memo);
    }
    
    public List<Memo> getMemosByCategory(Long categoryId) {
        return memoRepository.findByCategoryId(categoryId);
    }
    
    public List<Memo> getPinnedMemos(Long userId) {
        return memoRepository.findPinnedMemos(userId);
    }
    
    public List<Memo> getPendingMemos(Long userId) {
        return memoRepository.findPendingMemos(userId);
    }
    
    public Long getCompletedCount(Long userId) {
        return memoRepository.countCompletedMemos(userId);
    }
    
    public Long getTotalCount(Long userId) {
        return memoRepository.countTotalMemos(userId);
    }
}