package com.korit.security_study2.service;

import com.korit.security_study2.dto.ApiRespDto;
import com.korit.security_study2.dto.ModifyPasswordReqDto;
import com.korit.security_study2.entity.User;
import com.korit.security_study2.repository.UserRepository;
import com.korit.security_study2.security.model.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    public ApiRespDto<?> modifyPassword(ModifyPasswordReqDto modifyPasswordReqDto, Principal principal) {
        if (!modifyPasswordReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다!", principal.getUserId());
        }
        Optional<User> foundUser = userRepository.getUserByUserId(modifyPasswordReqDto.getUserId());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "그딴 인간은 없어요!", null);
        }
        if (!bCryptPasswordEncoder.matches(modifyPasswordReqDto.getOldPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "기존 비밀번호가 일치하지 않아요!", null);
        }
        if (bCryptPasswordEncoder.matches(modifyPasswordReqDto.getNewPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "새 비밀번호는 기존 비밀번호와 달라야 해요!", null);
        }
        int result = userRepository.updatePassword(modifyPasswordReqDto.toEntity(bCryptPasswordEncoder));
        if (result != 1) {
            return new  ApiRespDto<>("failed", "문제 발생", null);
        }
        return new ApiRespDto<>("success", "비밀번호 변경 완료~!@!@!@", null);
    }
}
