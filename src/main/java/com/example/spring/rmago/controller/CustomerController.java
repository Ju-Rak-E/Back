package com.example.spring.rmago.controller;

import com.example.spring.rmago.service.CustomerService;
import com.example.spring.rmago.swagger.CustomerControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController implements CustomerControllerDocs {
    private final CustomerService customerService;
}
