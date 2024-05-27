package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.PromocionesModel;

public interface IPromocionesService {
    // Guardar promocion 
    String guardarPromocion(PromocionesModel promocion,int idUsuario, String username);
    // Buscar promocion por Id
    PromocionesModel buscarPromocionPorId(int IdPromociones);
    // listar promocion
    List<PromocionesModel> listarPromociones();
    // Eliminar promocion
    void eliminarPromocionesPorId(int IdPromociones, int idUsuario, String username);
    //Actualizar promocion
    String actualizarpromocionPorId(PromocionesModel promocion, int idUsuario, String username);

}