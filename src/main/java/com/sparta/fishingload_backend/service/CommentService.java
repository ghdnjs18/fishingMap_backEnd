package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.dto.CommentRequestDto;
import com.sparta.fishingload_backend.dto.CommentResponseDto;
import com.sparta.fishingload_backend.dto.MessageResponseDto;
import com.sparta.fishingload_backend.entity.*;
import com.sparta.fishingload_backend.repository.CommentLikeRepository;
import com.sparta.fishingload_backend.repository.CommentRepository;
import com.sparta.fishingload_backend.repository.PostRepository;
import com.sparta.fishingload_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        Post post = findPost(requestDto.getPostId());

        String userid = user.getUserId();
        User user_selcet = findUser(userid);

        Comment comment = new Comment(requestDto);
        comment.setAccountId(userid);

        post.addCommentList(comment);
        user_selcet.addCommentList(comment);

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = findComment(id);

        if (!user.getUserId().equals(comment.getAccountId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 수정할 수 있습니다.");
        }
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> deleteComment(Long id, User user) {
        Comment comment = findComment(id);

        if (!user.getUserId().equals(comment.getAccountId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 삭제할 수 있습니다.");
        }

        comment.setCommentUse(false);

        MessageResponseDto message = new MessageResponseDto("댓글 삭제를 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> likeComment(Long id, User user) {
        Comment comment = findComment(id);
        User userSelect = findUser(user.getUserId());
        CommentLike commentLike = commentLikeRepository.findByUser_IdAndComment_Id(userSelect.getId(), id);

        if (commentLike == null) {
            commentLike = commentLikeRepository.save(new CommentLike(user, comment));
            comment.addCommentLikeList(commentLike);
        }

        MessageResponseDto message;
        if (commentLike.isCheck()) {
            commentLike.changeCheck();
            comment.setCommentLike(comment.getCommentLike() + 1);
            message = new MessageResponseDto("댓글 좋아요를 성공했습니다.", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }

        commentLike.changeCheck();
        comment.setCommentLike(comment.getCommentLike() - 1);
        message = new MessageResponseDto("댓글 좋아요를 취소했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    public CommentResponseDto createCommentComment(Long commentId, CommentRequestDto requestDto, User user) {
        String userId = user.getUserId();
        User user_selcet = findUser(userId);

        Comment commentSelect = findComment(commentId);

        Comment comment = new Comment(requestDto);
        comment.setAccountId(userId);

        user_selcet.addCommentList(comment);
        commentSelect.addCommentList(comment);

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    private Comment findComment(Long id) {
        return commentRepository.findByIdAndCommentUseTrue(id).orElseThrow(() ->
                new NullPointerException("선택한 댓글은 존재하지 않습니다.")
        );
    }

    private Post findPost(Long id) {
        return postRepository.findByIdAndPostUseTrue(id).orElseThrow(() ->
                new NullPointerException("선택한 게시물은 존재하지 않습니다.")
        );
    }

    private User findUser(String userId) {
        return userRepository.findByUserIdAndAccountUseTrue(userId).orElseThrow(() ->
                new NullPointerException("해당 유저는 존재하지 않습니다.")
        );
    }
}
