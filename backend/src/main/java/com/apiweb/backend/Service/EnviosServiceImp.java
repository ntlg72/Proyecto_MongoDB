package com.apiweb.backend.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.EnviosModel;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Repository.IEnviosRepository;
import com.apiweb.backend.Repository.IOrdenesRepository;

@Service

public class EnviosServiceImp implements IEnviosService {
    @Autowired IEnviosRepository enviosRepository;
    @Autowired IOrdenesRepository  ordenesRepository;

    @Override
    public String guardarEnvio(EnviosModel envio) {

        Integer ordenId = envio.getIdorden();
        OrdenesModel orden = ordenesRepository.findById(ordenId).orElse(null);
        
        if (orden == null) {
            return "Error! La orden con el ID " +ordenId+ " no fue encontrado en la BD.";
        }

        List<String> ciudades = Arrays.asList(
            "Medellín", "Barranquilla", "Bogotá", "Cartagena", "Tunja",
            "Manizales", "Florencia", "Popayán", "Valledupar", "Montería",
            "Zipaquirá", "Quibdó", "Neiva", "Riohacha", "Santa Marta"
        );
        String ciudadEnvio = envio.getDetallesubicacion().getCiudad();
        if (!ciudades.contains(ciudadEnvio)) {
            return "Error! El envío a la ciudad seleccionada no está disponible.";
        }

        enviosRepository.save(envio);

        return "El envío " + envio.getId() + " fue creado con éxito.";
    }


    @Override
    public EnviosModel buscarEnvioPorId(int idEnvio) {
        Optional <EnviosModel> envioRecuperado = enviosRepository.findById(idEnvio);
        return  envioRecuperado.orElseThrow(()-> new RecursoNoEncontradoException(
            "Error!.El envio con el Id " +idEnvio+ " no fue encontrado en la BD."));
    }



    @Override
    public List<EnviosModel> listarEnvios() {
        return enviosRepository.findAll();
    }


    @Override
    public void eliminarEnvioPorId(int idEnvio) {
        if (!enviosRepository.existsById(idEnvio)) {
            throw new RecursoNoEncontradoException(
            "Error!. El envio con el Id " + idEnvio + " no fue encontrado en la BD.");
        }
        
        enviosRepository.deleteById(idEnvio);
    }
    
}
