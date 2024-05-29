package com.apiweb.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apiweb.backend.Service.IOrdenesService;
import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.TopProductomenos;
import com.apiweb.backend.Model.TopProductos;
import com.apiweb.backend.Model.TopUsuariosCompras;


@RestController
@RequestMapping("/apiweb/ordenes")
public class OrdenesController {

    @Autowired IOrdenesService ordenesService;

    // Guardar Orden 
    @PostMapping("/guardar")
    public ResponseEntity<String> crearOrden(@RequestBody OrdenesModel orden) {
        try {
            String resultado = ordenesService.guardarOrden(orden);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
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

    //Actualizar Ordenes por id
    @PatchMapping("/actualizarorden/{id}/{idusuario}")
    public ResponseEntity<String> actualizarOrden(
        @PathVariable("id") int id, 
        @PathVariable("idusuario") int idusuario,
        @RequestBody OrdenesModel ordenDetalles) {
       try {
           String resultado = ordenesService.actualizarOrden(id,idusuario,ordenDetalles);
           return ResponseEntity.ok(resultado);
       } catch (RecursoNoEncontradoException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       }
    }
     @GetMapping("/top-ventas")
    public ResponseEntity<List<TopProductos>> getTop3ProductosMasVendidos() {
        List<TopProductos> topProductos = ordenesService.getTop3ProductosMasVendidos();
        return new ResponseEntity<List<TopProductos>>(topProductos, HttpStatus.OK);
    }
    
    @GetMapping ("/UsuariosTop")
    public ResponseEntity<List<TopUsuariosCompras>> topUsuarioConMasCompras(){
        List<TopUsuariosCompras> compra = ordenesService.topUsuarioConMasCompras();
        return new ResponseEntity<List<TopUsuariosCompras>>(compra, HttpStatus.OK);
    }

    @GetMapping("/topmenosVentas")
     public ResponseEntity<List<TopProductomenos>> getTop3ProductosMenosVendidos(){
         List<TopProductomenos> topProductomenos = ordenesService.getTop3ProductosMenosVendidos();
         return new ResponseEntity<List<TopProductomenos>>(topProductomenos, HttpStatus.OK );
    }
        

}