package com.sparta.fishingload_backend.dto;

import com.sparta.fishingload_backend.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponseDto {
    private String userId;
    private String nickname;
    private String email;
    private String profil;

    public AccountResponseDto(User user) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profil = user.getUserImage().getImageUrl();
    }
}
