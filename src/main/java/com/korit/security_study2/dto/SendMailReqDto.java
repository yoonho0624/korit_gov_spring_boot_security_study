package com.korit.security_study2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMailReqDto {
    private String email;
}
