package com.korit.security_study2.service;

import com.korit.security_study2.dto.ApiRespDto;
import com.korit.security_study2.dto.OAuth2MergeReqDto;
import com.korit.security_study2.dto.OAuth2SignupReqDto;
import com.korit.security_study2.entity.User;
import com.korit.security_study2.entity.UserRole;
import com.korit.security_study2.repository.OAuth2UserRepository;
import com.korit.security_study2.repository.UserRepository;
import com.korit.security_study2.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// oauth2로 회원가입 또는 연동
@Service
public class OAuth2AuthService {
    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "중복 email", null);
        }

        Optional<User> foundUsername = userRepository.getUserByUsername(oAuth2SignupReqDto.getUsername());
        if (foundUsername.isPresent()) {
            return new ApiRespDto<>("failed", "중복 username", null);
        }

        Optional<User> optionalUser = userRepository.addUser(oAuth2SignupReqDto.toUserEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);
        oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(optionalUser.get().getUserId()));

        return new ApiRespDto<>("success", oAuth2SignupReqDto.getProvider() + "로 회원가입 완료", null);
    }

    public ApiRespDto<?> merge(OAuth2MergeReqDto oAuth2MergeReqDto) {
        Optional<User> foundUser = userRepository.getUserByUsername(oAuth2MergeReqDto.getUsername());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "그런 인간은 없어요!", null);
        }

        if (!bCryptPasswordEncoder.matches(oAuth2MergeReqDto.getPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "그런 인간은 없어요!", null);
        }

        oAuth2UserRepository.addOAuth2User(oAuth2MergeReqDto.toEntity(foundUser.get().getUserId()));
        return new ApiRespDto<>("success", "★연동 완료★", null);
    }
}
