package com.apiweb.backend.Repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.OrdenesModel;

public interface IOrdenesRepository extends MongoRepository <OrdenesModel,Integer>{
    List<OrdenesModel> findByIdusuario(int idusuario);
}
