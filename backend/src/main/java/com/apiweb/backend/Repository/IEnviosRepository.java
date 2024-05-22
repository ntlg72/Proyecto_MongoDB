package com.apiweb.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.EnviosModel;

public interface IEnviosRepository extends MongoRepository<EnviosModel,Integer> {
    
}
