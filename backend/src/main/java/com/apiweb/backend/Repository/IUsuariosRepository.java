package com.apiweb.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.UsuariosModel;

public interface IUsuariosRepository extends MongoRepository<UsuariosModel,Integer>{
    
}