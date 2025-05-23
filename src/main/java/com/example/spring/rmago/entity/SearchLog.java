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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "input_amount")
    private Integer inputAmount;

    @Column(name = "search_lat")
    private Double searchLat;

    @Column(name = "search_lng")
    private Double searchLng;

    @Column(name = "result_count")
    private Integer resultCount;

    @Column(name = "delete_flag")
    private String deleteFlag = "N";
}
