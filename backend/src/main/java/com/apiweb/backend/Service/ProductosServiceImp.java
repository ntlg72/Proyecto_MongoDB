package com.apiweb.backend.Service;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.Comentario;
import com.apiweb.backend.Model.Contiene;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.ProductosPaquete;
import com.apiweb.backend.Model.TotalProducto;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.ValoracionAlta;
import com.apiweb.backend.Model.productosMasComentarios;
import com.apiweb.backend.Model.ENUM.TipoUsuario;
import com.apiweb.backend.Repository.IProductosRepository;
import com.apiweb.backend.Repository.IUsuariosRepository;
import com.apiweb.backend.Repository.IOrdenesRepository;

@Service
public class ProductosServiceImp implements IProductosService {
    @Autowired IProductosRepository productosRepository;
    @Autowired IOrdenesRepository ordenesRepository;
    @Autowired IUsuariosRepository usuariosRepository;

    @Override
    public String guardarProducto(ProductosModel productos, int idUsuario, String username){

        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        // Verificar si el usuario ya tiene una cuenta de administrador
        boolean esAdministrador = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipousuario() == TipoUsuario.administrador);

        if (!esAdministrador) {
            throw new RecursoNoEncontradoException("Error! El usuario no es administrador");
        }

        productosRepository.save(productos);
        return "El producto "+productos.getNombre()+" con el id "+productos.getId()+" fue creado con exito";
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
    public void eliminarProductoPorId(int idProducto, int idUsuario, String username) {
        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        // Verificar si el usuario ya tiene una cuenta de administrador
        boolean esAdministrador = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipousuario() == TipoUsuario.administrador);

        if (!esAdministrador) {
            throw new RecursoNoEncontradoException("Error! El usuario no es administrador");
        }

        if (!productosRepository.existsById(idProducto)) {
        throw new RecursoNoEncontradoException("Error!. El producto con el Id " +idProducto+ " no fue encontrado en la BD.");
    }
    productosRepository.deleteById(idProducto);
    }

    @Override
    public String guardarProductoPaquete(ProductosModel producto, int idUsuario, String username) {
        // Verificar si el usuario existe
        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));
    
        // Verificar si el usuario ya tiene una cuenta de administrador
        boolean esAdministrador = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipousuario() == TipoUsuario.administrador);
    
        if (!esAdministrador) {
            throw new RecursoNoEncontradoException("Error! El usuario no es administrador");
        }
    
        // Verificar que todos los productos en el paquete existen
        for (ProductosPaquete productosPaquete : producto.getProductospaquete()) {
            Integer idProducto = productosPaquete.getIdProducto();
            ProductosModel productoExistente = productosRepository.findById(idProducto).orElse(null);
            if (productoExistente == null) {
                return "Error: el producto con id: " + idProducto + " no existe.";
            }
        }
    
        // Guardar el producto paquete
        productosRepository.save(producto);
        return "El Producto Paquete con el Id " + producto.getId() + " fue creado con éxito.";
    }
    

    @Transactional
    public String guardarComentario(int idProducto, int idUsuario, Comentario comentario) {
    // Listar las órdenes realizadas por un usuario
    List<OrdenesModel> ordenes = ordenesRepository.findByIdusuario(idUsuario);

    // Verificar si alguna de las órdenes contiene el producto y está pagada
    boolean haOrdenadoYPagado = false;

    for (OrdenesModel orden : ordenes) {
        for (Contiene contiene : orden.getContiene()) {
            if (contiene.getIdproducto().equals(idProducto) && orden.getPago() != null && orden.getPago().isPagado()) {
                haOrdenadoYPagado = true;
                break;
            }
        }
        if (haOrdenadoYPagado) {
            break; // Si ya ha ordenado el producto y está pagada, sal del bucle de órdenes
        }
    }

    if (!haOrdenadoYPagado) {
        throw new RecursoNoEncontradoException("Error! El usuario no ha comprado el producto o la orden no está pagada");
    }

    ProductosModel producto = productosRepository.findById(idProducto)
            .orElseThrow(() -> new RecursoNoEncontradoException("El producto con el Id " + idProducto + " no fue encontrado"));

    comentario.setIdusuario(idUsuario);
    producto.getComentario().add(comentario);
    productosRepository.save(producto);

    return "El comentario fue creado con éxito";
    
    }

    // actualizar producto 
    @Transactional
    public String actualizarProducto(ProductosModel productos, int idUsuario) {
         // Verificar si el usuario existe en la base de datos
        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        // Verificar si el usuario ya tiene una cuenta de administrador
        boolean esAdministrador = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipousuario() == TipoUsuario.administrador);

        if (!esAdministrador) {
            throw new RecursoNoEncontradoException("Error! El usuario no es administrador");
        }
        // Verificar si el producto existe en la base de datos
        ProductosModel productoExistente = productosRepository.findById(productos.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El producto con el Id " + productos.getId() + " no fue encontrado en la BD."));

        // Actualizar los detalles del producto
        productoExistente.setNombre(productos.getNombre());
        productoExistente.setDescripcion(productos.getDescripcion());
        productoExistente.setPrecio(productos.getPrecio());
        productoExistente.setTalla(productos.getTalla());

        // Guardar el producto actualizado
        productosRepository.save(productoExistente);

        return "El producto " + productoExistente.getNombre() + " con el id " + productos.getId() + " fue actualizado con éxito";

    }
    @Override
    public List<ValoracionAlta> obtenerValoracionesAltas() {
        return productosRepository.obtenerValoracionesAltas();
    }

    @Override
    public List<productosMasComentarios> findProductosMasComentarios() {
        return productosRepository.findproductosMasComentarios();
    }

    @Override
    public List<TotalProducto> obtenerTotalCantidadPorProducto(){
        return productosRepository.obtenerTotalCantidadPorProducto();
    }
}