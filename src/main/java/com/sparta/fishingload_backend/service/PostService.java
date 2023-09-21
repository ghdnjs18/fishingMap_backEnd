package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.dto.MessageResponseDto;
import com.sparta.fishingload_backend.dto.PostListResponseDto;
import com.sparta.fishingload_backend.dto.PostRequestDto;
import com.sparta.fishingload_backend.dto.PostResponseDto;
import com.sparta.fishingload_backend.entity.*;
import com.sparta.fishingload_backend.repository.CategoryRepository;
import com.sparta.fishingload_backend.repository.PostLikeRepository;
import com.sparta.fishingload_backend.repository.PostRepository;
import com.sparta.fishingload_backend.repository.UserRepository;
import com.sparta.fishingload_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto);
        post.setAccountId(user.getUserId());

        if (user.getRole() == UserRoleEnum.ADMIN) post.setPoint(true);

        Category category = findCategory(requestDto.getCategoryId());
        category.addPostList(post);

        User userSelect = findUser(user.getUserId());
        userSelect.addPostList(post);
        postRepository.save(post);

        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public PostListResponseDto getPosts() {
        PostListResponseDto responseDto = new PostListResponseDto();
        List<Long> cate = new ArrayList<>();
        cate.add(1L);
        cate.add(2L);
        List<PostResponseDto> list = postRepository.findAllByCategoryIdInAndPostUseTrueAndPointTrue(cate).stream().map(PostResponseDto::new).toList();
        for(PostResponseDto postResponseDto : list){
            responseDto.setPost(postResponseDto);
        }
        return responseDto;
    }

    @Transactional(readOnly = true)
    public PostListResponseDto getMypost(User user) {
        PostListResponseDto responseDto = new PostListResponseDto();
        List<Long> cate = new ArrayList<>();
        cate.add(1L);
        cate.add(2L);
        List<PostResponseDto> list = postRepository.findAllByCategoryIdInAndAccountIdAndPostUseTrue(cate , user.getUserId()).stream().map(PostResponseDto::new).toList();
        for(PostResponseDto postResponseDto : list){
            responseDto.setPost(postResponseDto);
        }
        return responseDto;
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getCommunity(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PostResponseDto> pageList = postRepository.findAllByCategoryIdAndPostUseTrue(3L, pageable).map(PostResponseDto::new);
        for(PostResponseDto postResponseDto : pageList){
            commentChange(postResponseDto);
        }

        return pageList;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = findPost(id);
        PostResponseDto responseDto = new PostResponseDto(post);
        commentChange(responseDto);

        return responseDto;
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {
        Post post = findPost(id);

        if (!user.getUserId().equals(post.getAccountId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 게시물의 작성자만 수정할 수 있습니다.");
        }
        post.update(requestDto);
        return new PostResponseDto(post);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> deletePost(Long id, User user) {
        Post post = findPost(id);

        if (!user.getUserId().equals(post.getAccountId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 게시물의 작성자만 삭제할 수 있습니다.");
        }

        post.setPostUse(false);
        for (Comment comment : post.getCommentList()) {
            comment.setCommentUse(false);
        }

        MessageResponseDto message = new MessageResponseDto("게시물 삭제를 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> likePost(Long id, User user) {
        Post post = findPost(id);
        User userSelect = findUser(user.getUserId());
        PostLike postLike = postLikeRepository.findByUser_IdAndPost_Id(userSelect.getId(), id);

        if (postLike == null) {
            postLike = postLikeRepository.save(new PostLike(user, post));
            post.addPostLikeList(postLike);
        }

        MessageResponseDto message;
        if (postLike.isCheck()) {
            postLike.setCheck(false);
            post.setPostLike(post.getPostLike() + 1);
            message = new MessageResponseDto("게시물 좋아요를 성공했습니다.", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }

        postLike.setCheck(true);
        post.setPostLike(post.getPostLike() - 1);
        message = new MessageResponseDto("게시물 좋아요를 취소했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
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

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 카테고리는 존재하지 않습니다.")
        );
    }

    public void commentChange(PostResponseDto postResponseDto) {
        for (Comment comment : postResponseDto.getCommentList()) {
            commentSetChange(comment);
        }
    }

    private void commentSetChange(Comment comment) {
        if (!comment.isCommentUse()) {
            comment.setAccountId("알수없음");
            comment.setComment("삭제된 댓글입니다.");
        }
        if (comment.getChildcommentList() != null) {
            for (Comment comment1 : comment.getChildcommentList()) {
                commentSetChange(comment1);
            }
        }
    }

}
