package com.apiweb.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.CuentaUsuario;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Service.IUsuariosService;

@RestController
@RequestMapping("/apiweb/usuarios")

public class UsuariosController {
    @Autowired IUsuariosService usuariosService;

    @PostMapping("/crearusuario")
    public ResponseEntity<String> guardarUsuario (@RequestBody UsuariosModel usuario) {
    try {
        String mensajeRespuesta = usuariosService.guardarUsuario(usuario);
        return ResponseEntity.ok(mensajeRespuesta);
    } catch (DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
    //buscar usuario por id
    @GetMapping("/buscarporid/{id}")
    public ResponseEntity <?>  buscarUsuarioPorId (@PathVariable("id") int idUsuario){
        try{
            UsuariosModel usuario = usuariosService.buscarUsuarioPorId(idUsuario);
            return ResponseEntity.ok(usuario);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //listar usuarios
    @GetMapping("/listarusuarios")
    public ResponseEntity<List<UsuariosModel>> listarUsuarios() {
        List<UsuariosModel> usuarios = usuariosService.listarUsuarios();
        return new ResponseEntity<List<UsuariosModel>>(usuarios, HttpStatus.OK);
    }

    //eliminar usuario por id
    @DeleteMapping("/eliminarporid/{id}")
    public ResponseEntity<?> eliminarUsuarioPorId(@PathVariable("id") int idUsuario) {
        try {
            usuariosService.eliminarUsuarioPorId(idUsuario);
            return ResponseEntity.ok("El usuario con el Id " +idUsuario+ " fue eliminado correctamente");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Error al eliminar al usuario");
        }
    }
        
    //listar las cuentas de un usuario por id
    @GetMapping("/buscarcuentasporid/{id}")
    public ResponseEntity<?> cuentasUsuario(@PathVariable("id") int idUsuario) {
        try {
            List<CuentaUsuario> cuentas = usuariosService.cuentasUsuarios(idUsuario);
            return ResponseEntity.ok(cuentas);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //agregar cuenta a un usuario
    @PatchMapping("/{id}/cuentas")
    public ResponseEntity<?> agregarCuentaUsuario(
            @PathVariable("id") int idUsuario,
            @RequestBody CuentaUsuario nuevaCuentaUsuario) {
        try {
            usuariosService.agregarCuentaUsuario(idUsuario, nuevaCuentaUsuario);
            return ResponseEntity.ok("Cuenta de usuario agregada correctamente.");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //eliminar cuenta de un usuario (por ej, eliminar cuenta de cliente y dejar la de admin)
    @DeleteMapping("eliminarcuenta/{id}/{username}")
    public ResponseEntity<?> eliminarCuentaUsuario(
            @PathVariable("id") int idUsuario,
            @PathVariable("username") String username) {
        try {
            usuariosService.eliminarCuentaUsuario(idUsuario, username);
            return ResponseEntity.ok("Cuenta de usuario eliminada correctamente.");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //actualizar username o contrasena de cuenta
    @PatchMapping("/{id}/cuenta/{username}")
    public ResponseEntity<?> actualizarCuentaUsuario(
        @PathVariable("id") int idUsuario,
        @PathVariable("username") String username,
        @RequestBody CuentaUsuario nuevaCuentaUsuario) {
    try {
        usuariosService.actualizarCuentaUsuario(idUsuario, username, nuevaCuentaUsuario);
        return ResponseEntity.ok("Cuenta de usuario actualizada correctamente.");
    } catch (RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario ya está en uso.");
    }
}
    



    //actualizar email
    @PatchMapping("/{id}/email")
    public ResponseEntity<?> actualizarInformacionContacto(
        @PathVariable("id") int idUsuario,
        @RequestBody String nuevoEmail) {
    try {
        usuariosService.actualizarInformacionContacto(idUsuario, nuevoEmail);
        return ResponseEntity.ok("Email actualizado correctamente.");
    } catch (RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (DataIntegrityViolationException e) {
        // Determinar si la excepción está relacionada con el email
        String mensaje;
        if (e.getMessage().contains("email")) {
            mensaje = "El correo electrónico ya está en uso.";
        } else {
            mensaje = "Error al actualizar el email.";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }
}
}