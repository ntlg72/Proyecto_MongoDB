package com.apiweb.backend.Repository;

import java.util.List;


import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.ProductosPorCategoria;
import com.apiweb.backend.Model.TotalProducto;
import com.apiweb.backend.Model.ValoracionAlta;
import com.apiweb.backend.Model.productosMasComentarios;


public interface IProductosRepository extends MongoRepository<ProductosModel, Integer>{
    @Aggregation(pipeline = {
        "{ $unwind: { path: '$comentario' } }",
        "{ $match: { 'comentario.valoracion': { $in: ['cuatro', 'cinco'] } } }",
        "{ $project: { _id: 0, nombre: '$nombre', valoracion: '$comentario.valoracion' } }"
    })
    List<ValoracionAlta> obtenerValoracionesAltas();


    @Aggregation(pipeline ={
        " $unwind: { path: '$comentarios' } }",
        "{ $group: {_id: {Nombre: '$nombre', comentarios: '$comentarios.comentario'},TotalComentarios: { $sum: 1 }}}",
        "{ $sort: { TotalComentarios: -1 } }",
        "{$project: {_id: 0,Producto: '$_id.Nombre',TotalComentarios: 1}}"

    })
    List<productosMasComentarios> findproductosMasComentarios();

    @Aggregation(pipeline={
        "{ $unwind: { path: '$talla' } }",
    "{$group: {_id: { id: '$_id', Producto: '$nombre' },TotalProducto: { $sum: '$talla.cantidad' } } }",
    "{$project: {_id: 0,idProducto: '$_id.id',Producto: '$_id.Producto',TotalProducto: 1}}"
    })
    List<TotalProducto>  obtenerTotalCantidadPorProducto();

    @Aggregation(pipeline = {
        "{$group: {_id: '$categoria', TotalPCategoria: { $sum: 1 }}}",
        "{$project: {_id: 0, Categoria: '$_id', TotalPCategoria: 1}}"
    })
    List<ProductosPorCategoria> productosPorCategoria();
}