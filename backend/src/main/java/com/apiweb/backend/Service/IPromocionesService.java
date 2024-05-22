package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.PromocionesModel;

public interface IPromocionesService {
    // Guardar Producto
    String guardarPromocion(PromocionesModel promocion,int idUsuario, String username);
    // Buscar Producto por Id
    PromocionesModel buscarPromocionPorId(int IdPromociones);
    // listar Productos
    List<PromocionesModel> listarPromociones();
    // eliminar Producto por id
    void eliminarPromocionesPorId(int IdPromociones);
}