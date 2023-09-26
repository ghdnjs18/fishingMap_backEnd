package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage , Long> {
}
