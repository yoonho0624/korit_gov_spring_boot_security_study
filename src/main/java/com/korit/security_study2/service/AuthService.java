package com.korit.security_study2.service;

import com.korit.security_study2.dto.ApiRespDto;
import com.korit.security_study2.dto.SigninReqDto;
import com.korit.security_study2.dto.SignupReqDto;
import com.korit.security_study2.entity.User;
import com.korit.security_study2.entity.UserRole;
import com.korit.security_study2.repository.UserRepository;
import com.korit.security_study2.repository.UserRoleRepository;
import com.korit.security_study2.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private JwtUtils jwtUtils;

    public ApiRespDto<?> signup(SignupReqDto signupReqDto) {
        // username 중복 검사
        Optional<User> foundUser = userRepository.getUserByUsername(signupReqDto.getUsername());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "해당 username이 존재합니다.", null);
        }
        // 추가
        Optional<User> optionalUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        // role 추가
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);
        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", optionalUser.get());
    }

    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        // username을 가진 정보가 있는지 확인
        Optional<User> foundUser = userRepository.getUserByUsername(signinReqDto.getUsername());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "그런 인간은 없어요!", null);
        }
        User user = foundUser.get();
        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            return new ApiRespDto<>("failed", "그런 인간은 없어요!", null);
        }
        String token = jwtUtils.generateAccessToken(user.getUserId().toString());
        return new ApiRespDto<>("success", "로그인 성공", token);
    }

    public ApiRespDto<?> getUserByUsername(String username) {
        Optional<User> foundUser = userRepository.getUserByUsername(username);
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "그런 인간은 없어요!", null);
        }
        return new ApiRespDto<>("success", "회원조회 완료", foundUser.get());
    }
}
