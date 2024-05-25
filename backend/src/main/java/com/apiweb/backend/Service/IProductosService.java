package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.Comentario;
import com.apiweb.backend.Model.ProductosModel;

public interface IProductosService {
    // Guardar Producto
    String guardarProducto(ProductosModel Productos);
    // Buscar Producto por Id
    ProductosModel buscarProductoPorId(int idProducto);
    // listar Productos
    List<ProductosModel> listarProductos();
    // eliminar Producto por id
    void eliminarProductoPorId(int idProducto);
    // actualizar producto por id
    // Guardar Producto paquete
    String guardarProductoPaquete (ProductosModel producto); 
    //Guardar comentario
    String guardarComentario (int idProducto, int idUsuario, Comentario comentario );
}