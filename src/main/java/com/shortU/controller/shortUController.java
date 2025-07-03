package com.shortU.controller;

import com.shortU.model.shortUMap;
import com.shortU.service.shortUService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class shortUController {


        @Autowired
        private shortUService service;

        @PostMapping("/shorten")
        public ResponseEntity<?> createShortUrl( @RequestBody shortUMap urlMapping, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(errors);
            }

            try {
                shortUMap savedMapping = service.createShortUrl(urlMapping.getUrl());
                Map<String, Object> response = new HashMap<>();
                response.put("id", savedMapping.getShortCode());
                response.put("url", savedMapping.getUrl());
                response.put("shortCode", savedMapping.getShortCode());
                response.put("createdOn", savedMapping.getCreatedOn());
                response.put("updatedOn", savedMapping.getUpdatedOn());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error creating short URL: " + e.getMessage());
            }
        }
        @GetMapping("/shorten/all")
        public ResponseEntity<?> getAllUrlMappings() {
            List<shortUMap> urlMappings = service.getAllUrlMappings();

            if (!urlMappings.isEmpty()) {
                return ResponseEntity.ok(urlMappings);
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        @GetMapping("/shorten/{shortCode}")
        public ResponseEntity<?> getOriginalUrl(@PathVariable String shortCode) {
            Optional<shortUMap> urlMapping = service.getOriginalUrl(shortCode);
            if (urlMapping.isPresent()) {
                shortUMap mapping = urlMapping.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", mapping.getShortCode());
                response.put("url", mapping.getUrl());
                response.put("shortCode", mapping.getShortCode());
                response.put("createdAt", mapping.getCreatedOn());
                response.put("updatedAt", mapping.getUpdatedOn());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/{shortCode}")
        public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortCode) {
            Optional<shortUMap> urlMapping = service.getOriginalUrl(shortCode);
            if (urlMapping.isPresent()) {
                shortUMap mapping = urlMapping.get();
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", mapping.getUrl())
                        .build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @PutMapping("/shorten/{shortCode}")
        public ResponseEntity<?> updateShortUrl(@PathVariable String shortCode, @Valid @RequestBody shortUMap urlMapping, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(errors);
            }

            Optional<shortUMap> updatedMapping = service.updateShortUrl(shortCode, urlMapping.getUrl());
            if (updatedMapping.isPresent()) {
                shortUMap mapping = updatedMapping.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", mapping.getShortCode());
                response.put("url", mapping.getUrl());
                response.put("shortCode", mapping.getShortCode());
                response.put("createdAt", mapping.getCreatedOn());
                response.put("updatedAt", mapping.getUpdatedOn());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @DeleteMapping("/shorten/{shortCode}")
        public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
            boolean deleted = service.deleteShortUrl(shortCode);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/shorten/{shortCode}/stats")
        public ResponseEntity<?> getUrlStats(@PathVariable String shortCode) {
            Optional<shortUMap> urlMapping = service.getUrlStats(shortCode);
            if (urlMapping.isPresent()) {
                shortUMap mapping = urlMapping.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", mapping.getShortCode());
                response.put("url", mapping.getUrl());
                response.put("shortCode", mapping.getShortCode());
                response.put("createdAt", mapping.getCreatedOn());
                response.put("updatedAt", mapping.getUpdatedOn());
                response.put("accessCount", mapping.getAccessCount());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

