package com.example.spring.rmago.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

@Entity
@Filter(name = "deletedFilter")
@Getter
@Setter
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_log_id")
    private SearchLog searchLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    private Double distance;

    @Column(name = "estimated_fare")
    private Integer estimatedFare;

    @Column(name = "delete_flag")
    private String deleteFlag = "N";
}

