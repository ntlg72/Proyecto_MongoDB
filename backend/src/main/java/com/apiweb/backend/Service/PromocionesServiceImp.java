package com.apiweb.backend.Service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.ProductoPromocion;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.PromocionesModel;
import com.apiweb.backend.Model.UsuariosModel;
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
    @Override
    public String guardarPromocion(PromocionesModel promocion, int idUsuario, String username){


    UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

        // Verificar si el usuario ya tiene una cuenta de administrador
        boolean esAdministrador = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipousuario() == TipoUsuario.administrador);

        if (!esAdministrador) {
            throw new RecursoNoEncontradoException("Error! Solo los administradores pueden crear promociones.");
        }

        for (ProductoPromocion productos : promocion.getProductos()) {
            Integer id = productos.getIdproducto();
            ProductosModel producto = productosRepository.findById(id).orElse(null);
            if (producto == null) {
                throw new RecursoNoEncontradoException("Error! El producto con id: " + id + " no existe.");
            }
            productos.setIdproducto(id);
        }
        promocionesRepository.save(promocion);
        return "La promoción con el Id: " + promocion.getId() + " fue creada con éxito.";
    }

    // bucar una promocion por su id
    @Override
    public PromocionesModel buscarPromocionPorId(int IdPromociones){
        Optional<PromocionesModel> promocionRecuperado = promocionesRepository.findById(IdPromociones);
        return promocionRecuperado.orElseThrow(() -> new RecursoNoEncontradoException(
            "Error! La promocion con el Id"+ IdPromociones+" no fue encontrada"
        ));
    }

    // Listar todas las promociones
    @Override
    public List<PromocionesModel> listarPromociones(){
        return promocionesRepository.findAll();
    }

    @Override
    public void eliminarPromocionesPorId(int IdPromociones){
        if(!promocionesRepository.existsById(IdPromociones)){
            throw new RecursoNoEncontradoException("Error!. La promocion con el Id "+IdPromociones+" no fue encontrando.");
        }
    promocionesRepository.deleteById(IdPromociones);
    }

}