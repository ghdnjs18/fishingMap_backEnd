package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.User;
import com.sparta.fishingload_backend.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    UserImage findByUser(User user);
}
