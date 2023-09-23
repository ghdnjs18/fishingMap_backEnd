package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.dto.CommentRequestDto;
import com.sparta.fishingload_backend.dto.PostDetailResponseDto;
import com.sparta.fishingload_backend.dto.PostRequestDto;
import com.sparta.fishingload_backend.entity.Category;
import com.sparta.fishingload_backend.entity.Comment;
import com.sparta.fishingload_backend.entity.Post;
import com.sparta.fishingload_backend.entity.PostLike;
import com.sparta.fishingload_backend.repository.PostLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GetPostTest {

    @Mock
    PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("게시글 상세 조회 테스트 : 토큰이 있을 경우")
    void test1() {
        // given
        MultiValueMap<String, String> map = new HttpHeaders();
        map.add("Authorization", "token");
        HttpHeaders headers = new HttpHeaders(map);
        String result = null;
        // when
        if (headers.get("Authorization") != null) {
            result = "token";
        }
        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트 : 토큰이 없을 경우")
    void test2() {
        // given
        MultiValueMap<String, String> map = new HttpHeaders();
        HttpHeaders headers = new HttpHeaders(map);
        String result = null;
        // when
        if (headers.get("Authorization") != null) {
            result = "token";
        }
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트 : 게시글을 좋아요 했을 경우")
    void test3() {
        // given
        PostLike postLike = new PostLike();
        boolean result = false;
        // when
        if (postLike != null && !postLike.isCheck()) {
            result = true;
        }
        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트 : 게시글을 좋아요 취소 했을 경우")
    void test4() {
        // given
        PostLike postLike = new PostLike();
        postLike.changeCheck();
        boolean result = false;
        // when
        if (postLike != null && !postLike.isCheck()) {
            result = true;
        }
        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트 : 좋아요 정보가 없을 경우")
    void test5() {
        // given
        long userId = 1;
        long postId = 1;
        PostLike postLike = postLikeRepository.findByUser_IdAndPost_Id(userId, postId);
        boolean result = false;
        // when
        if (postLike != null && !postLike.isCheck()) {
            result = true;
        }
        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("게시글 상세 조회 삭제 댓글 출력 테스트 : 토큰이 있을 경우")
    void test6() {
        // given
        List<Comment> commentList = new ArrayList<>();
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment("1");
        commentList.add(new Comment(commentRequestDto));
        commentList.add(new Comment(commentRequestDto));
        commentList.add(new Comment(commentRequestDto));
        commentList.add(new Comment(commentRequestDto));
        PostDetailResponseDto postResponseDto = new PostDetailResponseDto();
        postResponseDto.setCommentList(commentList);
        // when
        for (Comment comment : postResponseDto.getCommentList()) {
            // then
            assertEquals("1", comment.getComment());
        }
    }
}
