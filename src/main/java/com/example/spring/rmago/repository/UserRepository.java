package com.example.spring.rmago.repository;

import com.example.spring.rmago.entity.Customer;

import java.util.Optional;

public interface UserRepository extends BaseRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
