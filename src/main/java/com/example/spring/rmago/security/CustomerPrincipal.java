package com.example.spring.rmago.security;

//최초 작성자 : 김병훈
//최초 작성일 : 2025-05-26
//OAuth2User 인증 후 Customer 정보와 속성(attribute)를 담아 반환하는 커스텀 유저 클래스
//읽기만 하는 불변객체이기에 Getter어노테이션만 사용

import com.example.spring.rmago.entity.Customer;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomerPrincipal implements OAuth2User {

    private final Customer customer;
    private final Map<String, Object> attributes;

    public CustomerPrincipal(Customer customer, Map<String, Object> attributes) {
        this.customer = customer;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한이 있다면 여기에 추가
    }

    @Override
    public String getName() {
        return customer.getEmail(); // 또는 kakaoId
    }

    public String getEmail() {
        return customer.getEmail();
    }
}
