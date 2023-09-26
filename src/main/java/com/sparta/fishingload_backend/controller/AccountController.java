package com.sparta.fishingload_backend.controller;

import com.sparta.fishingload_backend.dto.*;
import com.sparta.fishingload_backend.security.UserDetailsImpl;
import com.sparta.fishingload_backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    //개인정보
    @GetMapping("/account/myinfo")
    public AccountResponseDto MyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.MyInfo(userDetails.getUser());
    }

    //개인정보 수정
    @PutMapping("/account")
    public ResponseEntity<MessageResponseDto> UserUpdate ( @RequestBody UserUpdateRequestDto updateRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return accountService.UserUpdate(updateRequestDto, userDetails.getUser());
    }

    //My Page 회원이 작성한 게시글 (차후 +댓글 )
    @GetMapping("/account/mypage")
    public Page<PostResponseDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("isAsc") boolean isAsc) {
        return accountService.mypage(userDetails.getUser() ,page-1, size, sortBy, isAsc);
    }

    //비밀번호 체크
    //로그인 이후에 확인하는 것 같아서 account에 있음
    @PostMapping("/account/checkpw")
    public ResponseEntity<MessageResponseDto> checkPassword (@RequestBody CheckRequestDto checkRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return accountService.checkPassword(checkRequestDto,userDetails.getUser());
    }

    //회원 탈퇴
    @DeleteMapping("/account/resign")
    public ResponseEntity<MessageResponseDto> resign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return accountService.resign(userDetails.getUser());
    }

    // 회원 프로필 변경
    @PostMapping("/account/image")
    public ResponseEntity<MessageResponseDto> userImageUpload(@RequestPart("image") List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return accountService.userImageUpload(multipartFiles, userDetails.getUser());
    }
}
