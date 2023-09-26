package com.sparta.fishingload_backend.dto;

import com.sparta.fishingload_backend.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String accountId;
    private String contents;
    private int postLike;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String fishtype;
    private String locationdate;
    private List<Double> coordinates;
    private Category category;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.accountId = post.getAccountId();
        this.contents = post.getContents();
        this.postLike = post.getPostLike();
        this.createdTime = post.getCreatedTime();
        this.modifiedTime = post.getModifiedTime();
        this.fishtype = post.getFishtype();
        this.locationdate = post.getLocationdate();
        this.category = post.getCategory();
        this.coordinates = post.getCoordinates();
    }
}
