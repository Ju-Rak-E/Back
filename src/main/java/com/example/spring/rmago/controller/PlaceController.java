package com.example.spring.rmago.controller;

import com.example.spring.rmago.service.PlaceService;
import com.example.spring.rmago.swagger.PlaceControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController implements PlaceControllerDocs {
    private final PlaceService placeService;
}
