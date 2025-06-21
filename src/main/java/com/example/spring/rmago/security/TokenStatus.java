package com.example.spring.rmago.security;

public enum TokenStatus {
    VALID,     // 0: 정상
    EXPIRED,   // 1: 만료됨
    INVALID    // 2: 비정상 (형식 오류, 서명 오류 등)
}