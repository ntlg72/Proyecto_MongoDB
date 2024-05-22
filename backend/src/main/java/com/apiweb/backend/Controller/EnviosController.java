package com.apiweb.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.EnviosModel;
import com.apiweb.backend.Service.IEnviosService;

@RestController
@RequestMapping ("/apiweb/envios")

public class EnviosController {
    @Autowired
    private IEnviosService enviosService;

    @PostMapping ("/guardar")
    public ResponseEntity <String> guardarEnvio(@RequestBody EnviosModel envio){
        return new ResponseEntity<String>(enviosService.guardarEnvio(envio), HttpStatus.OK) ;
    }

    @GetMapping ("/buscar/{id}")
    public ResponseEntity<?> buscarEnvioPorId(@PathVariable Integer id){
        try {
        EnviosModel envio = enviosService.buscarEnvioPorId(id);
        return  ResponseEntity.ok(envio);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping ("/listarEnvio/")
    public ResponseEntity<List<EnviosModel>> listarEnvio() {
        List<EnviosModel> envio = enviosService.listarEnvios();
        return new ResponseEntity<List<EnviosModel>>(envio, HttpStatus.OK);
    }

    @DeleteMapping("/eliminarporid/{id}")
    public ResponseEntity<?> eliminarEnvioPorId(@PathVariable ("id") Integer id){
        try{
            enviosService.eliminarEnvioPorId(id);
            return ResponseEntity.ok("Envio eliminado correctamente");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el Envio");
        }

    }

}