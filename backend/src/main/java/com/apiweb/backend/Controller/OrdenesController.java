package com.apiweb.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apiweb.backend.Service.IOrdenesService;
import com.apiweb.backend.Model.OrdenesModel;


@RestController
@RequestMapping("/apiweb/ordenes")
public class OrdenesController {

    @Autowired IOrdenesService ordenesService;

    // Guardar Orden 
    @PostMapping("/guardar")
    public ResponseEntity<String> crearOrden(@RequestBody OrdenesModel orden) {
        return new ResponseEntity<String>(ordenesService.guardarOrden(orden),HttpStatus.OK);
    }

    //Buscar orden por id 
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarOrdenPorId(@PathVariable Integer id) {
        try {
            OrdenesModel orden = ordenesService.buscarOrdenPorId(id);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Ordenar todas las ordenes
    @GetMapping("/")
    public ResponseEntity<List<OrdenesModel>> listarOrdenes() {
        List<OrdenesModel> ordenes = ordenesService.listarOrdenes();
        return new ResponseEntity<List<OrdenesModel>>(ordenes, HttpStatus.OK);
    }
   
   //Actualizar Orden
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOrden(@PathVariable Integer id, @RequestBody OrdenesModel ordenDetalles) {
        String ordenActualizada = ordenesService.actualizarOrden(id, ordenDetalles);
        return ordenActualizada != null ? new ResponseEntity<>(ordenActualizada, HttpStatus.OK)
                                        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Eliminar Ordenes por id 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOrdenPorId(@PathVariable Integer id) {
    try {
        ordenesService.eliminarOrdenPorId(id);
        return ResponseEntity.ok("La orden con el ID " + id + " fue eliminada con éxito.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }

}