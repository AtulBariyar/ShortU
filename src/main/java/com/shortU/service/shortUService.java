package com.shortU.service;


import com.shortU.model.shortUMap;
import com.shortU.repository.shortURepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class shortUService {

        @Autowired
        private shortURepository repository;

        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        private static final int SHORT_CODE_LENGTH = 6;

       @Transactional
        public shortUMap createShortUrl(String originalUrl) {
            String shortCode = generateUniqueShortCode();
            shortUMap urlMapping = new shortUMap();
            urlMapping.setShortCode(shortCode);
            urlMapping.setUrl(originalUrl);
            return repository.save(urlMapping);
        }

       @Transactional
        public Optional<shortUMap> getOriginalUrl(String shortCode) {
            Optional<shortUMap> urlMapping = repository.findById(shortCode);
            if (urlMapping.isPresent()) {
                shortUMap mapping = urlMapping.get();
                mapping.setAccessCount(mapping.getAccessCount() + 1);
                return Optional.of(repository.save(mapping));
            }
            return Optional.empty();
        }

        @Transactional(readOnly = true)
        public Optional<shortUMap> getUrlStats(String shortCode) {
            return repository.findById(shortCode);
        }

        @Transactional
        public Optional<shortUMap> updateShortUrl(String shortCode, String newUrl) {
            Optional<shortUMap> existingMapping = repository.findById(shortCode);
            if (existingMapping.isPresent()) {
                shortUMap mapping = existingMapping.get();
                mapping.setUrl(newUrl);
                mapping.setUpdatedOn(LocalDateTime.now());
                return Optional.of(repository.save(mapping));
            }
            return Optional.empty();
        }
        @Transactional
        public List<shortUMap> getAllUrlMappings() {
            return repository.findAll();
        }
        @Transactional
        public boolean deleteShortUrl(String shortCode) {
            if (repository.existsById(shortCode)) {
                repository.deleteById(shortCode);
                return true;
            }
            return false;
        }
        private String generateUniqueShortCode() {
            Random random = new Random();
            String shortCode;
            int attempts = 0;
            do {
                StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
                for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                    sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
                }
                shortCode = sb.toString();
                attempts++;
                if (attempts > 10) {
                    throw new RuntimeException("Failed to generate a unique short code after 10 attempts");
                }
            } while (repository.existsById(shortCode));
            return shortCode;
        }
    }

