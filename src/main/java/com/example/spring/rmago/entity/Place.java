package com.example.spring.rmago.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Filter(name = "deletedFilter")
@Getter
@Setter
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;

    @Column(name = "place_image_url")
    private String placeImageUrl;

    private Double lat;
    private Double lng;

    @Column(name = "delete_flag")
    private String deleteFlag = "N";

    private String address;
    private String description;
}
