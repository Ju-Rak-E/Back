package com.example.spring.rmago.dto;

//최초 작업자 : 김병훈
//작성일 : 2025-05-30

//카카오에서의 accessToken을 Flutter를 통해 받은 값


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginRequestDto {
    private String accessToken;
}
