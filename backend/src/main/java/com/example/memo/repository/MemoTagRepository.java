package com.example.memo.repository;

import com.example.memo.entity.MemoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemoTagRepository extends JpaRepository<MemoTag, Long> {
    List<MemoTag> findByMemoId(Long memoId);
    List<MemoTag> findByTagId(Long tagId);
    void deleteByMemoId(Long memoId);
}