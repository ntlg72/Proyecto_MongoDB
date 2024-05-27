package com.apiweb.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.PromocionesModel;
import com.apiweb.backend.Model.productosPromo;

public interface IPromocionesRepository extends MongoRepository<PromocionesModel, Integer >{
    
    @Aggregation(pipeline = {
        "{ $unwind: { path: '$productos' } }",
        "{ $lookup: {from: 'Productos',localField: 'productos.idproductos',foreignField: '_id',as: 'promociones'}}",
        "{ $unwind: { path: '$promociones' } }",
        "{$project: {id: 0, Producto: '$promociones.nombre'}}"
    })

    List<productosPromo> findproductosPromo();
}