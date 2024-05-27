package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.TopProductomenos;
import com.apiweb.backend.Model.TopProductos;
import com.apiweb.backend.Model.TopUsuariosCompras;

public interface IOrdenesService {
    
    String guardarOrden(OrdenesModel orden);
    
    OrdenesModel buscarOrdenPorId(int idOrden);

    List<OrdenesModel> listarOrdenes();

    void eliminarOrdenPorId(int idOrden);

    String actualizarOrden(int id, int idusuario, OrdenesModel ordenDetalles);

    List<TopProductos> getTop3ProductosMasVendidos();

    List<TopUsuariosCompras> topUsuarioConMasCompras();

    List<TopProductomenos> getTop3ProductosMenosVendidos();


    
}