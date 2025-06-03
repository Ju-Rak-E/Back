package com.example.spring.rmago.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Filter(name = "deletedFilter")
@Getter
@Setter
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_log_id", nullable = false)
    private SearchLog searchLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "estimated_fare")
    private Integer estimatedFare;
}