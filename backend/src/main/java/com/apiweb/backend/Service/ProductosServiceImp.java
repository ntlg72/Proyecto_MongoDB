package com.apiweb.backend.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.ProductosPaquete;
import com.apiweb.backend.Repository.IProductosRepository;

@Service
public class ProductosServiceImp implements IProductosService {
    @Autowired IProductosRepository productosRepository;

    @Override
    public String guardarProducto(ProductosModel productos){
        productosRepository.save(productos);
        return "El producto "+productos.getNombre()+" del usuario con el id "+productos.getId()+" fue creado con exito";
    }

    @Override
    public ProductosModel buscarProductoPorId(int idProducto){
        Optional<ProductosModel> productoEncontrado = productosRepository.findById(idProducto);
        return productoEncontrado.orElseThrow(() -> new RecursoNoEncontradoException(
            "Error! El producto con el Id"+ idProducto+". No fue encontardo"
            ));
    }
    

    @Override
    public List<ProductosModel> listarProductos() {
    return productosRepository.findAll();
    }

    @Override
    public void eliminarProductoPorId(int idProducto) {
        if (!productosRepository.existsById(idProducto)) {
        throw new RecursoNoEncontradoException("Error!. El usuario con el Id " +idProducto+ " no fue encontrado en la BD.");
    }
    productosRepository.deleteById(idProducto);
    }

    @Override
    public String guardarProductoPaquete (ProductosModel producto) {
        for (ProductosPaquete ProductosPaquete : producto.getProductospaquete()){
            Integer id = ProductosPaquete.getIdProducto();
            ProductosModel Producto = productosRepository.findById(id).orElse(null);
            if(Producto == null){
                return "Error el producto con id: "+id+", no existe";
            }
            ProductosPaquete.setIdProducto(id);
        }
        productosRepository.save(producto);
        return "El Producto Paquete con el Id"+ producto.getId()+ "fue creado con exito";   
    }
    
}