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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id")
    private String kakaoId;

    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    private String email;

    private String gender;

    private LocalDate birth;

    @Column(name = "delete_flag")
    private String deleteFlag = "N";
}

