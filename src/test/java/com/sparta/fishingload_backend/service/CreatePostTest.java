package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.entity.Category;
import com.sparta.fishingload_backend.entity.User;
import com.sparta.fishingload_backend.entity.UserRoleEnum;
import com.sparta.fishingload_backend.repository.CategoryRepository;
import com.sparta.fishingload_backend.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
//@DataJpaTest // JPA 의존주입을 할 수 있게 해주는 선언
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 임베디드 데이터 베이스를 사용 안한다는 선언
class CreatePostTest {

//    @Autowired // 의존성 주입
    @Mock
    CategoryRepository categoryRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("게시글 작성 유저 권한 테스트 : USER 경우")
    void test1() {
        // given
        String result = null;
        User user = new User();
        user.setRole(UserRoleEnum.USER);
        // when
        if (user.getRole() == UserRoleEnum.ADMIN) result = "admin";
        // then
        assertNull(result);
    }
    // 어떻게 user role이 null이 였다 치고 테스트도 있어야하는가 ?
    @Test
    @DisplayName("게시글 작성 유저 권한 테스트 : ADMIN 경우")
    void test2() {
        // given
        String result = null;
        User user = new User();
        user.setRole(UserRoleEnum.ADMIN);
        // when
        if (user.getRole() == UserRoleEnum.ADMIN) result = "admin";
        // then
        assertNotNull(result);
        assertEquals("admin", result);
    }

    @Test
    @DisplayName("게시글 작성 카테고리 찾기 성공 테스트")
    @Disabled
    // Repository 단위 테스트를 하면 실제 데이터베이스를 불러와 의존성 주입이 되기 떄문에 DB에 영향이 받는다.
    void test3() {
        // given
        long id = 1;
        // when
        Category result = categoryRepository.findById(id).orElse(null);
        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("게시글 작성 카테고리 찾기 예러처리 테스트")
    void test4() {
        // given
        long id = 3;
        // when
        NullPointerException result = assertThrows(NullPointerException.class, () ->
                categoryRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 카테고리는 존재하지 않습니다.")));
        // then
        assertEquals("해당 카테고리는 존재하지 않습니다.", result.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 유저 찾기 성공 테스트")
    @Disabled
    void test5() {
        // given
        String userId = "user";
        // when
        User result = userRepository.findByUserIdAndAccountUseTrue(userId).orElse(null);
        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("게시글 작성 유저 찾기 에러처리 테스트")
    void test6() {
        // given
        String userId = "user";
        // when
        NullPointerException result = assertThrows(NullPointerException.class, () ->
                userRepository.findByUserIdAndAccountUseTrue(userId).orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다.")));
        // then
        assertEquals("해당 유저는 존재하지 않습니다.", result.getMessage());
    }
}