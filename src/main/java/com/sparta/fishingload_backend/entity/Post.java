package com.sparta.fishingload_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.fishingload_backend.dto.PostRequestDto;
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
@Table(name = "post")
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    @Column(name="account_id",nullable = false)
    private String accountId;

    @Column(name = "postlike", nullable = false)
    private int postLike = 0;

    @Column(name = "post_use", nullable = false)
    private boolean postUse = true;

    @Column(name = "fishtype")
    private String fishtype;

    @Column(name = "locationdate")
    private String locationdate;

    @Column(name = "point", nullable = false)
    private boolean point = false;

    @ElementCollection
    @CollectionTable(name = "coordinates")
    private List<Double> coordinates = new ArrayList<>();

    @ManyToOne
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikeList = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "post_id")
    @OrderBy("createdTime desc")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostImage> postImages = new ArrayList<>();

    public Post(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.fishtype = requestDto.getFishtype();
        this.locationdate = requestDto.getLocationdate();
        this.coordinates = requestDto.getCoordinates();
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.fishtype = requestDto.getFishtype();
        this.locationdate = requestDto.getLocationdate();
        this.coordinates = requestDto.getCoordinates();
    }

    public void addPostLikeList(PostLike postLike) {
        this.postLikeList.add(postLike);
        postLike.setPost(this);
    }

    public void addCommentList(Comment comment) {
        this.commentList.add(comment);
    }

    public void addPostImageList(PostImage postImage) {
        this.postImages.add(postImage);
    }
}
