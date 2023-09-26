package com.sparta.fishingload_backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.fishingload_backend.dto.*;
import com.sparta.fishingload_backend.entity.*;
import com.sparta.fishingload_backend.repository.*;
import com.sparta.fishingload_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final AmazonS3Client amazonS3Client;
    private final JwtUtil jwtUtil;
    private final PostImageRepository postImageRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public PostResponseDto createPost(MultipartFile[] multipartFiles, PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto);
        post.setAccountId(user.getUserId());

        if (user.getRole() == UserRoleEnum.ADMIN) post.setPoint(true);

        Category category = findCategory(requestDto.getCategoryId());
        category.addPostList(post);

        User userSelect = findUser(user.getUserId());
        userSelect.addPostList(post);

        List<PostImage> postImage = upload(multipartFiles);

        postRepository.save(post);
        for (PostImage image : postImage) {
            image.setPost(post);
        }
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

        return pageList;
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto getPost(Long id, HttpHeaders headers) {
        Post post = findPost(id);
        PostDetailResponseDto responseDto = new PostDetailResponseDto(post);

        if (headers.get("Authorization") != null) {
            String token = headers.getFirst("Authorization");
            token = jwtUtil.substringToken(token);
            String userId = jwtUtil.getUserInfoFromToken(token).getSubject();
            User user = findUser(userId);

            PostLike postLike = postLikeRepository.findByUser_IdAndPost_Id(user.getId(), id);
            if (postLike != null && !postLike.isCheck()) {
                responseDto.setPostLikeUse(true);
            }
            commentChange(responseDto, user.getId());
            return responseDto;
        }

        commentChange(responseDto, 0L);
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
            postLike.changeCheck();
            post.setPostLike(post.getPostLike() + 1);
            message = new MessageResponseDto("게시물 좋아요를 성공했습니다.", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }

        postLike.changeCheck();
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

    private void commentChange(PostDetailResponseDto postResponseDto, Long userId) {
        postResponseDto.getCommentList().removeIf(comment -> !comment.isCommentUse());
        for (Comment comment : postResponseDto.getCommentList()) {
            commentSetChange(comment, userId);
        }
    }

    private void commentSetChange(Comment comment, Long userId) {
        comment.getChildcommentList().removeIf(c -> !c.isCommentUse());
        CommentLike commentLike = commentLikeRepository.findByUser_IdAndComment_Id(userId, comment.getId());
        if (commentLike != null && !commentLike.isCheck()) {
            comment.setCommentLikeUse(true);
        }
        for (Comment childComment : comment.getChildcommentList()) {
            commentSetChange(childComment, userId);
        }
    }

    private List<PostImage> upload(MultipartFile[] multipartFiles) {

        List<PostImage> postImages = new ArrayList<>();

        String uploadFilePath = "postImage";

        for (MultipartFile multipartFile :  multipartFiles) {
            //파일 확장자 추출
            String filetype = multipartFile.getOriginalFilename().
                    substring(multipartFile.getOriginalFilename().indexOf(".")+1);
            //랜덤 이름 부여
            String uploadName = UUID.randomUUID()+"."+filetype;

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = uploadFilePath + "/" + uploadName;

                // S3에 폴더 및 파일 업로드
                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                );

                // S3에 업로드한 폴더 및 파일 URL
               String uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

               //이미지
               PostImage postImage = new PostImage();
               postImage.save(keyName,uploadFileUrl);

               postImageRepository.save(postImage);
               postImages.add(postImage);
            } catch (IOException e) {
                e.printStackTrace();
        }
        }
        return postImages;
    }

}
