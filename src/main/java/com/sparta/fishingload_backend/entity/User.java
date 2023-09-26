package com.sparta.fishingload_backend.entity;

import com.sparta.fishingload_backend.dto.SignupRequestDto;
import com.sparta.fishingload_backend.dto.UserUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid", nullable = false, unique = true)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "account_use")
    private boolean accountUse = true;

    @Column(name = "admin", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Post> postList = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Comment> commentList = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserImage userImage;

    public User(String userId, String password, UserRoleEnum role, SignupRequestDto requestDto) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.nickname = requestDto.getNickname();
        this.email = requestDto.getEmail();
    }

    public void addPostList(Post post) {
        this.postList.add(post);
    }

    public void addCommentList(Comment comment) {
        this.commentList.add(comment);
    }

    public void change(String password) {
        this.password = password;
    }
}