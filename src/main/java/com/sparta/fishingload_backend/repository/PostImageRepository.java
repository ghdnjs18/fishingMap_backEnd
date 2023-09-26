package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage , Long> {
    List<PostImage> findByPostId(Long id);
}
