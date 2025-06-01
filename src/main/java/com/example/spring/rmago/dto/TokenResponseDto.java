package com.example.spring.rmago.dto;

//최초 작업자 : 김병훈
//작성일 : 2025-05-30

//jwt 응답 DTO

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String AccessToken;
    private String RefreshToken;
}
