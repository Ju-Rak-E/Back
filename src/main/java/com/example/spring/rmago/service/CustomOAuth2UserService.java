package com.example.spring.rmago.service;

//최초 작성자 : 김병훈
//작성일 : 2025-05-24
//사용자 정보를 카카오에서 가져와 가공하는 역할

import com.example.spring.rmago.entity.Customer;
import com.example.spring.rmago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 계정 정보 파싱 예시
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) kakaoAccount.get("nickname");
        String kakaoId = (String) kakaoAccount.get("kakao_id");


        // DB에서 사용자 찾기 또는 새로 저장
        Customer customer = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(email);
                    newCustomer.setKakaoId(kakaoId);
                    newCustomer.setNickname(nickname);
                    // 필요한 필드들 추가 설정
                    return userRepository.save(newCustomer);
                });

// 커스텀 OAuth2User 반환
//        return new CustomOAuth2User(customer, oAuth2User.getAttributes());

    }
}