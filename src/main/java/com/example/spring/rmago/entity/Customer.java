package com.example.spring.rmago.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Filter(name = "deletedFilter")
public class Customer extends BaseEntity {

    @Column(name = "kakao_id", length = 50)
    private String kakaoId;

    @Column(name = "nickname", length = 100)
    private String nickname;

    // 실제로는 업로드된 파일 경로 또는 S3 업로드된 URL을 저장.
    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birth")
    private LocalDate birth;
}