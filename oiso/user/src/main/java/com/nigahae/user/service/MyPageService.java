package com.nigahae.user.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.user.controller.dto.MyPageResponse;
import com.nigahae.user.domain.User;
import com.nigahae.user.repository.MyPageQueryRepository;
import com.nigahae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final MyPageQueryRepository myPageQueryRepository;

    public MyPageResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        MyPageQueryRepository.Counts counts = myPageQueryRepository.findCounts(userId);

        return new MyPageResponse(
                new MyPageResponse.Profile(
                        user.getNickname(),
                        user.getProfileImageUrl()
                ),
                new MyPageResponse.SavedSpotSummary(
                        counts.savedSpotCount(),
                        myPageQueryRepository.findSavedSpots(userId).stream()
                                .map(row -> new MyPageResponse.SavedSpotItem(
                                        row.spotId(),
                                        row.imageUrl()
                                ))
                                .toList()
                ),
                new MyPageResponse.SavedStorySummary(
                        counts.savedStoryCount(),
                        myPageQueryRepository.findSavedStories(userId).stream()
                                .map(row -> new MyPageResponse.SavedStoryItem(
                                        row.storyId(),
                                        row.imageUrl()
                                ))
                                .toList()
                ),
                new MyPageResponse.MemoSummary(
                        counts.memoCount(),
                        myPageQueryRepository.findMemos(userId).stream()
                                .map(row -> new MyPageResponse.MemoItem(
                                        row.memoId(),
                                        row.content(),
                                        row.courseTitle()
                                ))
                                .toList()
                )
        );
    }
}
