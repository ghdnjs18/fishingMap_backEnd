package com.sparta.fishingload_backend.entity;

import com.sparta.fishingload_backend.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name="nickname",nullable = false)
    private String nickname;

    @Column(name = "postlike", nullable = false)
    private int postLike = 0;

    @Column(name = "post_use", nullable = false)
    private boolean postUse = true;

    @Column(name = "fishtype")
    private String fishtype;

    @Column(name = "locationdate")
    private String locationdate;

    @ManyToOne
    private Category category;

    public Post(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.fishtype = requestDto.getFishtype();
        this.locationdate = requestDto.getLocationdate();
    }
}
