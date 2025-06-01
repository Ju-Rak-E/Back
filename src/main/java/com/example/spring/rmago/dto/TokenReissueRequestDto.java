package com.example.spring.rmago.dto;

//최초 작성자: 김병훈
//작성일 : 2025-0-01
//리프레쉬 토큰 응답 Dto

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenReissueRequestDto {
    private String refreshToken;
}
