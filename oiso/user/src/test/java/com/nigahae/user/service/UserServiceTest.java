package com.nigahae.user.service;

import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.domain.User;
import com.nigahae.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final UserService service = new UserService(repository);

    @Test
    void registersFirstSocialLogin() {
        when(repository.findByProviderAndProviderUserId("kakao", "123")).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenReturn(User.builder().userId(7L).provider("kakao")
                .providerUserId("123").nickname("첫로그인").build());
        assertThat(service.loginOrRegister("kakao", "123", "첫로그인", null, null))
                .isEqualTo(new AuthenticatedUser(7L, "첫로그인"));
        verify(repository).save(any(User.class));
    }

    @Test
    void updatesExistingUserWithoutDuplicateInsert() {
        User existing = User.builder().userId(7L).provider("kakao").providerUserId("123")
                .nickname("이전").build();
        when(repository.findByProviderAndProviderUserId("kakao", "123")).thenReturn(Optional.of(existing));
        AuthenticatedUser result = service.loginOrRegister("kakao", "123", "변경", "new@example.com", "image");
        assertThat(result).isEqualTo(new AuthenticatedUser(7L, "변경"));
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        verify(repository, never()).save(any());
    }
}
