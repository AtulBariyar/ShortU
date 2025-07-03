package com.shortU.repository;

import com.shortU.model.shortUMap;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface shortURepository extends MongoRepository<shortUMap,String> {
}
