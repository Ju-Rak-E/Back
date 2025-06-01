package com.example.spring.rmago.dto;


//최초 작업자 : 김병훈
//작성일 : 2025-05-30

//사용자 정보 응답 DTO

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerInfoDto {

    private String email;
    private String kakaoId;
    private String profileImage;
}
