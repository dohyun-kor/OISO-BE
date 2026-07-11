package com.nigahae.user.service;

import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.UserAuthenticationService;
import com.nigahae.user.domain.User;
import com.nigahae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserAuthenticationService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthenticatedUser loginOrRegister(String provider, String providerUserId, String nickname,
                                              String email, String profileImageUrl) {
        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .map(existing -> {
                    existing.updateProfile(nickname, email, profileImageUrl);
                    return existing;
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .nickname(nickname)
                        .email(email)
                        .profileImageUrl(profileImageUrl)
                        .build()));

        return new AuthenticatedUser(user.getUserId(), user.getNickname());
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
