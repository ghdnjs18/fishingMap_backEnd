package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByCategoryIdAndPostUseTrue(Long categoryId, Pageable pageable);

    Optional<Post> findByIdAndPostUseTrue(Long id);

    Page<Post> findAllByPostUseTrueAndAccountId(Pageable pageable, String id);

    List<Post> findAllByCategoryIdInAndPostUseTrueAndPointTrue(Collection<Long> category_id);

    List<Post> findAllByCategoryIdInAndAccountIdAndPostUseTrue(Collection<Long> category_id, String accountId);
}
