package com.example.memo.controller;

import com.example.memo.entity.Memo;
import com.example.memo.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memos")
@CrossOrigin(origins = "*")
public class MemoController {
    
    @Autowired
    private MemoService memoService;
    
    @GetMapping
    public ResponseEntity<List<Memo>> getAllMemos(@RequestParam Long userId) {
        return ResponseEntity.ok(memoService.getAllMemos(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Memo> getMemoById(@PathVariable Long id) {
        return memoService.getMemoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Memo> createMemo(@RequestBody Memo memo) {
        return ResponseEntity.ok(memoService.createMemo(memo));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Memo> updateMemo(@PathVariable Long id, @RequestBody Memo memo) {
        return ResponseEntity.ok(memoService.updateMemo(id, memo));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/toggle-complete")
    public ResponseEntity<Memo> toggleComplete(@PathVariable Long id) {
        return ResponseEntity.ok(memoService.toggleComplete(id));
    }
    
    @PatchMapping("/{id}/toggle-pin")
    public ResponseEntity<Memo> togglePin(@PathVariable Long id) {
        return ResponseEntity.ok(memoService.togglePin(id));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Memo>> getMemosByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(memoService.getMemosByCategory(categoryId));
    }
    
    @GetMapping("/pinned")
    public ResponseEntity<List<Memo>> getPinnedMemos(@RequestParam Long userId) {
        return ResponseEntity.ok(memoService.getPinnedMemos(userId));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Memo>> getPendingMemos(@RequestParam Long userId) {
        return ResponseEntity.ok(memoService.getPendingMemos(userId));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats(@RequestParam Long userId) {
        Long completed = memoService.getCompletedCount(userId);
        Long total = memoService.getTotalCount(userId);
        return ResponseEntity.ok(Map.of("completed", completed, "total", total));
    }
}