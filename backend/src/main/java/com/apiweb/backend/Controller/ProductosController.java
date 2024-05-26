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
import com.apiweb.backend.Model.Comentario;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Service.IProductosService;

@RestController
@RequestMapping("/api/productos")

public class ProductosController {

    @Autowired IProductosService productoService;

    // Crear un nuevo producto
    @PostMapping("/guardar/{idusuario}/{username}")
    public ResponseEntity<String> guardarProducto(
        @RequestBody ProductosModel producto,
        @PathVariable("idusuario") int idUsuario,
        @PathVariable("username") String username) {
        return new ResponseEntity<String>(productoService.guardarProducto(producto,idUsuario,username),HttpStatus.OK);
    }

    // Obtener un producto por ID
    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<?> buscarProductoPorId(@PathVariable int id) {
        try {
            ProductosModel producto = productoService.buscarProductoPorId(id);
            return ResponseEntity.ok(producto);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Listar todos los productos
    @GetMapping("/listarproductos")
    public ResponseEntity<List<ProductosModel>> listarProductos() {
        List<ProductosModel> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    // Eliminar un producto por ID
    @DeleteMapping("eliminarporid/{id}/{idusuario}/{username}")
    public ResponseEntity<String> eliminarProductoPorId(
            @PathVariable("id") int idProducto,
            @PathVariable("idusuario") int idUsuario,
            @PathVariable("username") String username) {

        try {
            productoService.eliminarProductoPorId(idProducto, idUsuario, username);
            return ResponseEntity.ok("Producto con id " + idProducto + " eliminada correctamente");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la promoción: " + e.getMessage());
        }
    }


    // Crear un nuevo producto paquete
    @PostMapping("/crearpaquete/{idusuario}/{username}")
    public ResponseEntity<String> crearProductoPaquete(
        @RequestBody ProductosModel producto,
        @PathVariable("idusuario") int idUsuario,
        @PathVariable("username") String username
        ) {
        String id = productoService.guardarProductoPaquete(producto, idUsuario, username);
        return ResponseEntity.ok("Producto paquete creado con ID: " + id);
    }

    @PostMapping("/{idProducto}/{idusuario}/comentarios/guardar")
    public ResponseEntity<String> guardarComentario(
            @PathVariable("idProducto") int idproducto,
            @PathVariable("idusuario") int idusuario,
            @RequestBody Comentario comentario) {
        try {
            String resultado = productoService.guardarComentario(idproducto, idusuario, comentario);
            return ResponseEntity.ok(resultado);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
}