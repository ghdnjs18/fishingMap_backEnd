package com.sparta.fishingload_backend.controller;

import com.sparta.fishingload_backend.dto.*;
import com.sparta.fishingload_backend.security.UserDetailsImpl;
import com.sparta.fishingload_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public PostResponseDto createPost(@RequestPart ("images") MultipartFile[]  multipartFiles, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(multipartFiles ,requestDto, userDetails.getUser());
    }

    @GetMapping("/post")
    public PostListResponseDto getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/post/mypost")
    public PostListResponseDto getMypost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getMypost(userDetails.getUser());
    }

    @GetMapping("/post/community")
    public Page<PostResponseDto> getCommunity(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {
        return postService.getCommunity(page-1, size, sortBy, isAsc);
    }

    @GetMapping("/post/{id}")
    public PostDetailResponseDto getPost(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        return postService.getPost(id, headers);
    }

    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<MessageResponseDto> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails.getUser());
    }

    @PutMapping("/post/like/{id}")
    public ResponseEntity<MessageResponseDto> likePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.likePost(id, userDetails.getUser());
    }
}
