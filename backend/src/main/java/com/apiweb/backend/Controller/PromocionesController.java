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
import com.apiweb.backend.Model.PromocionesModel;
import com.apiweb.backend.Service.IPromocionesService;
import com.apiweb.backend.Service.IUsuariosService;

@RestController
@RequestMapping("/apiweb/promociones")

public class PromocionesController {
    @Autowired IPromocionesService promocionesService;
    @Autowired IUsuariosService usuariosService;


    @PostMapping ("crear/{id}/{username}")
    public ResponseEntity<String> guardarPromocion(
            @RequestBody PromocionesModel promocion,
            @PathVariable("id") int idUsuario,
            @PathVariable("username") String username) {

        try {
            String resultado = promocionesService.guardarPromocion(promocion, idUsuario, username);
            return ResponseEntity.ok(resultado);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping ("/buscarporid/{id}")
    public ResponseEntity<?> buscarPromocionPorId(@PathVariable ("id") int IdPromociones) {
    try {
        PromocionesModel promociones = promocionesService.buscarPromocionPorId(IdPromociones);
        return ResponseEntity.ok(promociones);
            
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }


    @GetMapping ("/listar/")
    public ResponseEntity<List<PromocionesModel>> listarPromociones() {
        List<PromocionesModel> promociones = promocionesService.listarPromociones();
        return ResponseEntity.ok(promociones);
    }
    
    @DeleteMapping("/eliminarporid/{idpro}/{iduser}/{username}")
    
    public ResponseEntity<String> eliminarPromocionPorId(
            @PathVariable("idpro") int idPromocion,
            @PathVariable("iduser") int idUsuario,
            @PathVariable("username") String username) {

        try {
            promocionesService.eliminarPromocionesPorId(idPromocion, idUsuario, username);
            return ResponseEntity.ok("Promoción con id " + idPromocion + " eliminada correctamente");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la promoción: " + e.getMessage());
        }
    }

    }
    

