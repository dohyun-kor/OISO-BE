package com.nigahae.user;

public interface UserAuthenticationService {
    AuthenticatedUser loginOrRegister(
            String provider,
            String providerUserId,
            String nickname,
            String email,
            String profileImageUrl
    );
}
