package com.sparta.fishingload_backend.repository;

import com.sparta.fishingload_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByUserIdAndAccountUseTrue(String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmailAndAccountUseTrue(String email);

    Optional<User> findByUserIdAndEmail(String userId, String email);

}
