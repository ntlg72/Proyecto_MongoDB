package com.apiweb.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.Contiene;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Repository.IOrdenesRepository;
import com.apiweb.backend.Repository.IProductosRepository;
import com.apiweb.backend.Repository.IUsuariosRepository;

@Service

public class OrdenesServiceImp implements IOrdenesService{

    @Autowired IOrdenesRepository ordenRepository;

    @Autowired IUsuariosRepository usuariosRepository;
    
    @Autowired IProductosRepository productoRepository;

    @Override
    public String guardarOrden(OrdenesModel orden) {
        int idUsuario = orden.getIdusuario(); // Obtiene el idUsuario de la orden

        // Verifica si el usuario existe
        Optional<UsuariosModel> usuarioOptional = usuariosRepository.findById(idUsuario);
        if (!usuarioOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Error! El usuario con el Id " + idUsuario + " no existe en la base de datos.");
        }

            // Verifica si todos los productos de la orden existen
            StringBuilder respuesta = new StringBuilder();
            boolean todosProductosExisten = true;

            for (Contiene contiene : orden.getContiene()) {
            Integer idProducto = contiene.getIdproducto();
            Optional<ProductosModel> productoOptional = productoRepository.findById(idProducto);

            if (!productoOptional.isPresent()) {
                respuesta.append("Error! El producto con el id ").append(idProducto).append(" no existe en la base de datos.\n");
                todosProductosExisten = false;
            }
        }

        // Si todos los productos existen, guarda la orden
            if (todosProductosExisten) {
            ordenRepository.save(orden);
            respuesta.append("La orden con el iD:"+ orden.getId()+" fue creada con éxito.");
        } else {
            respuesta.append("No se puede crear la orden porque uno o más productos no existen.");
        }

        return respuesta.toString().trim();
    }

    @Override
    public OrdenesModel buscarOrdenPorId(int idOrden) {
        Optional <OrdenesModel> ordenRecuperada = ordenRepository.findById(idOrden);
        return ordenRecuperada.orElseThrow(() -> new RecursoNoEncontradoException(
            "Error! La orden con el ID " + idOrden + " no fue encontrada."
        ));
    }

    @Override
    public List<OrdenesModel> listarOrdenes() {
        return ordenRepository.findAll();
    }

    @Override
    public void eliminarOrdenPorId(int idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new RecursoNoEncontradoException("Error! La orden con el ID " + idOrden + " no fue encontrada en la base de datos.");
        }
        ordenRepository.deleteById(idOrden);
    }

    @Override
    public String actualizarOrden(int idOrden, OrdenesModel ordenDetalles) {
        OrdenesModel ordenExistente = ordenRepository.findById(idOrden)
            .orElseThrow(() -> new RecursoNoEncontradoException("Error! La orden con el ID " + idOrden + " no fue encontrada."));
        
        // Actualizar los detalles de la orden existente
        ordenExistente.setFechaorden(ordenDetalles.getFechaorden());
        ordenExistente.setContiene(ordenDetalles.getContiene());
        
        ordenRepository.save(ordenExistente);
        return "La orden con el ID " + idOrden + " fue actualizada con éxito.";
    }
    
}
