package com.cheroliv.web.rest

import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CoucouResource {
    fun displayCoucou() {
    }

    @GetMapping("/coucou")
    fun getAllPeople(@org.springdoc.api.annotations.ParameterObject pageable: Pageable): ResponseEntity<String> {

        return ResponseEntity.ok().body("coucou")
    }
}
