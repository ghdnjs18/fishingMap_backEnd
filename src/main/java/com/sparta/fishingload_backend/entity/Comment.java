package com.sparta.fishingload_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.fishingload_backend.dto.CommentRequestDto;
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
@Table(name = "comment")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", nullable = false, length = 500)
    private String comment;

    @Column(name="account_id",nullable = false)
    private String accountId;

    @Column(name = "commentlike", nullable = false)
    private int commentLike = 0;

    @Column(name = "comment_use", nullable = false)
    private boolean commentUse = true;

    @Column(name = "commentlikeues", nullable = false)
    private boolean commentLikeUse = false;

    @JsonIgnore
    @OneToMany(mappedBy = "comment")
    private List<CommentLike> commentLikeList = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> ChildcommentList = new ArrayList<>();

    public Comment(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }

    public void update(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }

    public void addCommentLikeList(CommentLike commentLike) {
        this.commentLikeList.add(commentLike);
        commentLike.setComment(this);
    }

    public void addCommentList(Comment comment) {
        this.ChildcommentList.add(comment);
        comment.setParentComment(this);
    }
}
