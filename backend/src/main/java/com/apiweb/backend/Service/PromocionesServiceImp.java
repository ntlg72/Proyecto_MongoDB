package com.apiweb.backend.Service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.ProductoPromocion;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.PromocionesModel;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.productosPromo;
import com.apiweb.backend.Model.ENUM.TipoUsuario;
import com.apiweb.backend.Repository.IProductosRepository;
import com.apiweb.backend.Repository.IPromocionesRepository;
import com.apiweb.backend.Repository.IUsuariosRepository;

@Service
public class PromocionesServiceImp implements IPromocionesService{
    @Autowired IPromocionesRepository promocionesRepository;
    @Autowired IProductosRepository productosRepository;
    @Autowired IUsuariosRepository usuariosRepository;
    
    // Guardar un nueva promocion
    @Transactional//si ocurre cualquier error durante el procesamiento, todos los cambios realizados se revertirán
    public String guardarPromocion(PromocionesModel promocion, int idUsuario, String username){


            UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

            boolean esAdmin = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getUsername().equals(username) && cuenta.getTipousuario() == TipoUsuario.administrador);
            
            if (!esAdmin) {
                throw new RecursoNoEncontradoException("Error! Solo los administradores pueden crear promociones.");
            }

        for (ProductoPromocion productoPromocion : promocion.getProductos()) {
            Integer idProducto = productoPromocion.getIdproductos();
            Optional<ProductosModel> productoOptional = productosRepository.findById(idProducto);

            // Verificar si el producto existe
            if (!productoOptional.isPresent()) {
                throw new RecursoNoEncontradoException("Error! El producto con id: " + idProducto + " no existe.");
            }

            ProductosModel producto = productoOptional.get();

            // Calcular el nuevo precio basado en el descuento
            double descuento = promocion.getDescuento();
            double nuevoPrecio = producto.getPrecio() * (1 - descuento);

            // Actualizar el precio del producto
            producto.setPrecio(nuevoPrecio);
            productosRepository.save(producto);

            // Asegurarse de que el id del producto está correctamente establecido en la promoción
            productoPromocion.setIdproductos(idProducto);
        }
        promocionesRepository.save(promocion);

        return "La promoción con el Id: " + promocion.getId() + " fue creada con éxito.";
    }




    // bucar una promocion por su id
    @Override
    public PromocionesModel buscarPromocionPorId(int IdPromociones){
        Optional<PromocionesModel> promocionRecuperado = promocionesRepository.findById(IdPromociones);
        return promocionRecuperado.orElseThrow(() -> new RecursoNoEncontradoException(
        "Error! La promocion con el Id"+ IdPromociones+" no fue encontrada"));
    }




    // Listar todas las promociones
    @Override
    public List<PromocionesModel> listarPromociones(){
        return promocionesRepository.findAll();
    }




    
     // Eliminar una promocion
    @Transactional//si ocurre cualquier error durante el procesamiento, todos los cambios realizados se revertirán
    public void eliminarPromocionesPorId(int IdPromociones, int idUsuario, String username){
        
        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
        .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        boolean esAdmin = usuario.getCuentas().stream()
        .anyMatch(cuenta -> cuenta.getUsername().equals(username) && cuenta.getTipousuario() == TipoUsuario.administrador);
    
        if (!esAdmin) {
        throw new RecursoNoEncontradoException("Error! Solo los administradores pueden eliminar promociones.");
        }

    // Verificar si la promoción existe
    if (!promocionesRepository.existsById(IdPromociones)) {
        throw new RecursoNoEncontradoException("Error! La promoción con el Id " + IdPromociones + " no fue encontrada.");
    }

    PromocionesModel promocion = promocionesRepository.findById(IdPromociones)
            .orElseThrow(() -> new RecursoNoEncontradoException("Error! La promoción con el Id " + IdPromociones + " no fue encontrada."));

    // Iterar sobre los productos asociados a la promoción
    for (ProductoPromocion productoPromocion : promocion.getProductos()) {
        Integer idProducto = productoPromocion.getIdproductos();
        ProductosModel producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error! El producto con id: " + idProducto + " no existe."));

        // Calcular el precio original del producto
        double descuento = promocion.getDescuento();
        double precioConDescuento = producto.getPrecio();
        double precioOriginal = precioConDescuento / (1 - descuento);

        // Actualizar el precio del producto
        producto.setPrecio(precioOriginal);
        productosRepository.save(producto);
    }

    // Eliminar la promoción
    promocionesRepository.deleteById(IdPromociones);
    }


    //Actualizar promocion
    @Transactional
    public String actualizarpromocionPorId(PromocionesModel promocion, int idUsuario, String username){
        
        UsuariosModel usuario = usuariosRepository.findById(idUsuario) 
        .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        boolean esAdmin = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getUsername().equals(username) && cuenta.getTipousuario() == TipoUsuario.administrador);
            
        if (!esAdmin) {
                throw new RecursoNoEncontradoException("Error! Solo los administradores pueden actualizar promociones.");
            }
         PromocionesModel promocionExistente = promocionesRepository.findById(promocion.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: La promoción con el Id " + promocion.getId() + " no fue encontrada en la BD."));

        //Actualizar los datos
         promocionExistente.setDescuento(promocion.getDescuento());
         promocionExistente.setFechainicio(promocion.getFechainicio());
         promocionExistente.setFechafin(promocion.getFechafin());
        // Limpiar la lista de productos existente
        promocionExistente.getProductos().clear();
        //Actualizar los productos
        for (ProductoPromocion productoPromocion : promocion.getProductos()) {
            Integer idProducto = productoPromocion.getIdproductos();
            ProductosModel producto = productosRepository.findById(idProducto)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Error! El producto con id: " + idProducto + " no existe."));
        // Calcular el nuevo precio basado en el descuento
            double descuento = promocion.getDescuento();
            double nuevoPrecio = producto.getPrecio() * (1 - descuento);



        // Actualizar el precio del producto
        producto.setPrecio(nuevoPrecio);
        productosRepository.save(producto);

        // Asegurarse de que el id del producto está correctamente establecido en la promoción
        productoPromocion.setIdproductos(idProducto);

        // Añadir el producto a la promoción existente
        promocionExistente.getProductos().add(productoPromocion);
        }

        // Guardar la promoción actualizada
        promocionesRepository.save(promocionExistente);
        return "Promoción actualizada exitosamente";
        
    }


    @Override
    public List<productosPromo> findproductosPromo() {
        return promocionesRepository.findproductosPromo();
    }

}