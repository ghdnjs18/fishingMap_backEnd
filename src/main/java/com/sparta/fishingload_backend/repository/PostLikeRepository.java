package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByUser_IdAndPost_Id(Long userId, Long postId);
}
