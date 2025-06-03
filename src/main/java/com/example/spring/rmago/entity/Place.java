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

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "place_image_url", length = 255)
    private String placeImageUrl;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}