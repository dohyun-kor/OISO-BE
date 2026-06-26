package com.nigahae.user.service;

import com.nigahae.user.domain.User;
import com.nigahae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;



    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
