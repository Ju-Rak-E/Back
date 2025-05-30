package com.example.spring.rmago.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "search_log")
@Filter(name = "deletedFilter")
@Getter
@Setter
public class SearchLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "input_amount")
    private Integer inputAmount;

    @Column(name = "search_lat", precision = 10, scale = 6)
    private Double searchLat;

    @Column(name = "search_lng", precision = 10, scale = 6)
    private Double searchLng;

    @Column(name = "result_count")
    private Integer resultCount;
}