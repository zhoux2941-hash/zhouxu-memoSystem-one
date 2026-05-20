package com.example.memo.controller;

import com.example.memo.entity.Tag;
import com.example.memo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public ResponseEntity<List<Tag>> getTags(@RequestParam Long userId) {
        return ResponseEntity.ok(tagService.getTagsByUser(userId));
    }
    
    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok().build();
    }
}