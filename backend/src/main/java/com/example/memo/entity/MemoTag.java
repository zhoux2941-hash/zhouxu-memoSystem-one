package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "memo_tags")
@Data
public class MemoTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "memo_id")
    private Long memoId;
    
    @Column(name = "tag_id")
    private Long tagId;
}