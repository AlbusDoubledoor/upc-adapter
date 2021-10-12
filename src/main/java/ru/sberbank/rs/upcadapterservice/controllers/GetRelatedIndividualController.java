package ru.sberbank.rs.upcadapterservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetRelatedIndividualController {
    @GetMapping("/get-related-individual")
    public String getRelatedIndividual() {
        return "OK";
    }
}
