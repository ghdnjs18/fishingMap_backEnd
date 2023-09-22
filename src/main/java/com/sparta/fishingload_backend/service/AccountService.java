package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.dto.*;
import com.sparta.fishingload_backend.entity.Comment;
import com.sparta.fishingload_backend.entity.Post;
import com.sparta.fishingload_backend.entity.User;
import com.sparta.fishingload_backend.repository.PostRepository;
import com.sparta.fishingload_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    //개인 정보
    public AccountResponseDto MyInfo(User user) {
        User userSelect = userRepository.findById(user.getId()).orElseThrow(() ->
                new NullPointerException("유저 정보가 없습니다."));
        AccountResponseDto accountResponseDto = new AccountResponseDto(userSelect);
        return accountResponseDto;
    }

    //개인정보 수정
    public ResponseEntity<MessageResponseDto> UserUpdate(UserUpdateRequestDto updateRequestDto, User user) {
        // 입력값이 null일 경우
        if (updateRequestDto.getPassword() != null) {
            String password = passwordEncoder.encode(updateRequestDto.getPassword());
            user.setPassword(password);
        }
        if (updateRequestDto.getEmail() != null ) {
            String email = updateRequestDto.getEmail();
            user.setEmail(email);
        }
        if (updateRequestDto.getNickname() != null) {
            String nickname = updateRequestDto.getNickname();
            user.setNickname(nickname);
        }

        userRepository.save(user);
        MessageResponseDto messageResponseDto = new MessageResponseDto( "회원님의 정보를 수정하였습니다. ", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponseDto);
    }

    //마이페이지 -> 자신이 쓴 글
    public Page<PostResponseDto> mypage(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PostResponseDto> pageList = postRepository.findAllByPostUseTrueAndAccountId(pageable, user.getUserId()).map(PostResponseDto::new);

        return pageList;
    }

    // 패스워드 체크
    public ResponseEntity<MessageResponseDto> checkPassword(CheckRequestDto checkRequestDto, User user) {

        if (passwordEncoder.matches(checkRequestDto.getPassword() ,user.getPassword())) {
            MessageResponseDto messageResponseDto = new MessageResponseDto("비밀번호가 일치합니다.", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDto);
        }
        MessageResponseDto messageResponseDto = new MessageResponseDto("비밀번호가 틀립니다. " , HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponseDto);
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<MessageResponseDto> resign(User user) {
        User userselect = userRepository.findByUserId(user.getUserId()).orElse(null);

//         현재 게시물과 댓글이 없기 때문에 주석 처리함 이후 사용할  예정
        // 회원이 작성한 게시물 삭제 처리
        for (Post board : userselect.getPostList()) {
            board.setPostUse(false);
            for (Comment comment : board.getCommentList()) {
                comment.setCommentUse(false);
            }
        }
//         회원이 작성한 댓글 삭제 처리
        for (Comment comment : userselect.getCommentList()) {
            comment.setCommentUse(false);
        }
        userselect.setAccountUse(false);

        MessageResponseDto message = new MessageResponseDto("회원탈퇴가 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
