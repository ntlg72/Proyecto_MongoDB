package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.OrdenesModel;

public interface IOrdenesService {
    
    String guardarOrden(OrdenesModel orden);
    
    OrdenesModel buscarOrdenPorId(int idOrden);

    List<OrdenesModel> listarOrdenes();

    void eliminarOrdenPorId(int idOrden);

    String actualizarOrden(int id, OrdenesModel ordenDetalles);
}