package com.apiweb.backend.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.EnviosModel;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Repository.IEnviosRepository;
import com.apiweb.backend.Repository.IOrdenesRepository;

@Service

public class EnviosServiceImp implements IEnviosService {
    @Autowired IEnviosRepository enviosRepository;
    @Autowired IOrdenesRepository  ordenesRepository;

    private static final List<String> CIUDADES_DISPONIBLES = Arrays.asList(
        "Medellín", "Barranquilla", "Bogotá", "Cartagena", "Tunja",
        "Manizales", "Florencia", "Popayán", "Valledupar", "Montería",
        "Zipaquirá", "Quibdó", "Neiva", "Riohacha", "Santa Marta"
    );

    
    private Double obtenerPrecioEnvio(String ciudad) {
        switch (ciudad) {
            case "Medellín":
                return 10000.0;
            case "Barranquilla":
                return 15000.0;
            case "Bogotá":
                return 10000.0;
            case "Cartagena":
                return 15000.0;
            case "Tunja":
                return 12000.0;
            case "Manizales":
                return 17000.0;
            case "Florencia":
                return 18000.0;
            case "Popayán":
                return 11000.0;
            case "Valledupar":
                return 13000.0;
            case "Montería":
                return 14000.0;
            case "Zipaquirá":
                return 11000.0;
            case "Quibdó":
                return 19000.0;
            case "Neiva":
                return 12000.0;
            case "Riohacha":
                return 15000.0;
            case "Santa Marta":
                return 17000.0;
            default:
                return null;}
        }

    @Transactional
    public String guardarEnvio(EnviosModel envio) {

        Integer ordenId = envio.getIdorden();
        OrdenesModel orden = ordenesRepository.findById(ordenId).orElse(null);

        if (orden == null) {
            return "Error! La orden con el ID " + ordenId + " no fue encontrada en la BD.";
        }

        String ciudadEnvio = envio.getDetallesubicacion().getCiudad();
        if (!CIUDADES_DISPONIBLES.contains(ciudadEnvio)) {
            return "Error! El envío a la ciudad seleccionada no está disponible.";
        }

        Double valorEnvio = obtenerPrecioEnvio(ciudadEnvio);
        if (valorEnvio == null) {
            return "Error! No se encontró el precio de envío para la ciudad " + ciudadEnvio;
        }

        // Asignar el valor de envío al envío
        envio.setValorenvio(valorEnvio);
        // Guardar el envío
        enviosRepository.save(envio);

        // Actualizar el valor total de la orden con el valor de envío
        double nuevoValorTotal = orden.getSubtotal() + valorEnvio;
        orden.getPago().setValorpago(nuevoValorTotal);
        // Guardar la orden actualizada
        ordenesRepository.save(orden);

        return "El envío " + envio.getId() + " fue creado con éxito y el valor total de la orden se ha actualizado a:."+nuevoValorTotal;
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

    //Actualizar envio
    @Transactional
    public String actualizarEnvio(EnviosModel envio) {
        // Verificar si el envío existe en la base de datos
        EnviosModel envioExistente = enviosRepository.findById(envio.getId()).orElse(null);
        if (envioExistente == null) {
            return "Error! El envío con el ID " + envio.getId() + " no fue encontrado en la BD.";
        }
        // Verificar si la orden asociada al envío existe
        Integer ordenId = envio.getIdorden();
        OrdenesModel orden = ordenesRepository.findById(ordenId).orElse(null);
        if (orden == null) {
            return "Error! La orden con el ID " + ordenId + " no fue encontrada en la BD.";
        }
        //Verificar si la ciudad de envio esta disponible
        String ciudadEnvio = envio.getDetallesubicacion().getCiudad();
        if(!CIUDADES_DISPONIBLES.contains(ciudadEnvio)){
            return "Error! La ciudad de envio no esta disponible";
        }

        //Obtener precio de envio
        Double valorEnvio = obtenerPrecioEnvio(ciudadEnvio);
        if (valorEnvio == null) {
            return "Error! No se encontró el precio de envío para la ciudad " + ciudadEnvio;
        }
         // Actualizar datos de envio
        envioExistente.setDetallesubicacion(envio.getDetallesubicacion());
        envioExistente.setBarrio(envio.getBarrio());
        envioExistente.setDireccion(envio.getDireccion());
        envioExistente.setValorenvio(valorEnvio);


        // Guardar el envío actualizado
        enviosRepository.save(envioExistente);

        // Actualizar el valor total de la orden con el valor de envío
        double nuevoValorTotal = orden.getSubtotal() + valorEnvio;
        orden.getPago().setValorpago(nuevoValorTotal);
        // Guardar la orden actualizada
        ordenesRepository.save(orden);

        return "El envío " + envio.getId() + " fue actualizado con éxito y el valor total de la orden se ha actualizado a:"+nuevoValorTotal;
    }
    
}
