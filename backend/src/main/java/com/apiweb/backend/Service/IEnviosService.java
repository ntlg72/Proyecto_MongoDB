package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.EnviosModel;

public interface IEnviosService {
    //guardar envio
    String guardarEnvio(EnviosModel envio);
     //buscar envio por id
    EnviosModel buscarEnvioPorId(int idEnvio);
    //listar envios
    List<EnviosModel> listarEnvios();
     //eliminar envio por id
    void eliminarEnvioPorId(int idEnvio);
    //Actualizar envio
    String actualizarEnvio(EnviosModel envio);
}
