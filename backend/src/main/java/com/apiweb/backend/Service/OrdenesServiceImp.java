package com.apiweb.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.Contiene;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Repository.IOrdenesRepository;
import com.apiweb.backend.Repository.IProductosRepository;

@Service

public class OrdenesServiceImp implements IOrdenesService{

    @Autowired IOrdenesRepository ordenRepository;
    
    @Autowired IProductosRepository productoRepository;

    @Override
    public String guardarOrden(OrdenesModel orden) {
        for (Contiene contiene : orden.getContiene()) {
            Integer id = contiene.getIdproducto();
            ProductosModel prod = productoRepository.findById(id).orElse(null);
            if (prod == null) {
                return "Error! El producto con el id " + id + " no existe en la base de datos.";
            }
            contiene.setIdproducto(id);  // Supongo que esto es lo que querías hacer aquí
        }
        ordenRepository.save(orden);
        return "La orden " + orden.getId() + " fue creada con éxito.";
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
