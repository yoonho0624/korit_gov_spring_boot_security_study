package com.korit.security_study2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninReqDto {
    private String username;
    private String password;
}
