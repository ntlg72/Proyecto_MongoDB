package com.apiweb.backend.Repository;


import java.util.List;


import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.TopProductomenos;
import com.apiweb.backend.Model.TopProductos;
import com.apiweb.backend.Model.TopUsuariosCompras;

public interface IOrdenesRepository extends MongoRepository <OrdenesModel,Integer>{
    List<OrdenesModel> findByIdusuario(int idusuario);
    
     @Aggregation(pipeline = {
          "{ $unwind: '$contiene' }",
          "{ $group: { _id: '$contiene.idproducto', TotalVentas: { $sum: '$contiene.cantidad' } } }",
          "{ $sort: { TotalVentas: -1 } }",
          "{ $limit: 3 }",
          "{ $lookup: { from: 'Productos', localField: '_id', foreignField: '_id', as: 'producto' } }",
          "{ $unwind: '$producto' }",
          "{ $project: { idProducto: '$_id', nombre: '$producto.nombre', totalVentas: '$TotalVentas' } }"
      })
      List<TopProductos> findTop3ProductosMasVendidos();
      
    @Aggregation(pipeline={   
        "{$group: {_id: '$idusuario',Compras: { $sum: 1 }}}",
        "{$lookup: {from: 'Usuarios',localField: '_id',foreignField: '_id',as: 'usuario'}}",
        "{ $unwind: { path: '$usuario' } }",
        "{$project: {_id: 0Nombre: '$usuario.nombre',Compras: 1}}",
        "{ $sort: { Compras: -1 } }",
        "{ $limit: 5 }"
        })
    List<TopUsuariosCompras> topUsuarioConMasCompras();

    @Aggregation(pipeline={
    "{ $unwind: { path: '$contiene' } }",
    "{$group: { _id: '$contiene.idproducto',TotalVentas: { $sum: '$contiene.cantidad'}}}",
    "{ $sort: { TotalVentas: 1 } }",
    "{ $limit: 3 }",
    "{$lookup: {from: 'Productos',localField: '_id', foreignField: '_id',as: 'producto'}}",
    "{ $unwind: { path: '$producto' } }",
    "{$project: {_id: '$_id',Nombre: '$producto.nombre',TotalVentas: 1 }}"
    })
    List<TopProductomenos> getTop3ProductosMenosVendidos();

    
}
