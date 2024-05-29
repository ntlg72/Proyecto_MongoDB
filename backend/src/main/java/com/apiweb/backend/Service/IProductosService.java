package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.Comentario;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.ProductosPorCategoria;
import com.apiweb.backend.Model.TotalProducto;
import com.apiweb.backend.Model.ValoracionAlta;
import com.apiweb.backend.Model.productosMasComentarios;



public interface IProductosService {
    // Guardar Producto
    String guardarProducto(ProductosModel Productos,int idUsuario, String username);
    // Buscar Producto por Id
    ProductosModel buscarProductoPorId(int idProducto);
    // listar Productos
    List<ProductosModel> listarProductos();
    // eliminar Producto por id
    void eliminarProductoPorId(int idProducto,int idUsuario, String username);
    // actualizar producto por id
    // Guardar Producto paquete
    String guardarProductoPaquete(ProductosModel producto, int idUsuario, String username); 
    //Guardar comentario
    String guardarComentario (int idProducto, int idUsuario, Comentario comentario );
    //Actualizar Producto
    String actualizarProducto(ProductosModel productos, int idUsuario, String username);

    ///queries
    List<ValoracionAlta> obtenerValoracionesAltas();

    List<productosMasComentarios> findproductosMasComentarios();

    List<TotalProducto>  obtenerTotalCantidadPorProducto();

    List<ProductosPorCategoria> productosPorCategoria();
}