package com.apiweb.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.CuentaUsuario;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.ENUM.TipoUsuario;
import com.apiweb.backend.Repository.IUsuariosRepository;


@Service
public class UsuariosServiceImp implements IUsuariosService{
    @Autowired IUsuariosRepository usuariosRepository;
    

    @Override
    public String guardarUsuario(UsuariosModel usuario) {
        try {
            usuariosRepository.save(usuario);
            return "El usuario " + usuario.getId() + " fue creado con éxito.";
        } catch (DuplicateKeyException e) {
            String mensajeError = "";
            if (e.getMessage().contains("identificacion")) {
                mensajeError = "Error: La identificación " + usuario.getIdentificacion() + " ya existe.";
            } else if (e.getMessage().contains("email")) {
                mensajeError = "Error: El correo electrónico " + usuario.getEmail() + " ya está en uso.";
            } else if (e.getMessage().contains("username")) {
                mensajeError = "Error: El username ya está en uso.";
            } else {
                mensajeError = "Error desconocido: " + e.getMessage();
            }
            return mensajeError;
        }
    }
    
    


        
    @Override
    public UsuariosModel buscarUsuarioPorId(int id) {
        Optional <UsuariosModel> estudianteRecuperado = usuariosRepository.findById(id);
        return  estudianteRecuperado.orElseThrow(()-> new RecursoNoEncontradoException(
            "Error!. El usuario con el Id" +id+ ", no fue encontrado en la BD."));
    }


    @Override
    public List<UsuariosModel> listarUsuarios() {
        return usuariosRepository.findAll();
    }

    @Override
    public void eliminarUsuarioPorId(int idUsuario) {
        if (!usuariosRepository.existsById(idUsuario)) {
        throw new RecursoNoEncontradoException(
            "Error!. El usuario con el Id " +idUsuario+ " no fue encontrado en la BD.");
    }
    
    usuariosRepository.deleteById(idUsuario);
    }

    @Override
    public List<CuentaUsuario> cuentasUsuarios(int idUsuario) {
        
        UsuariosModel usuario = usuariosRepository.findById(idUsuario) .orElseThrow(()-> new
        RecursoNoEncontradoException("Error!.El usuario con el Id " +idUsuario+ " no fue encontrado en la BD."));
        
        List<CuentaUsuario> cuentas = usuario.getCuentas();
        
        return cuentas;
    }

    @Override
    public void agregarCuentaUsuario(int idUsuario, CuentaUsuario nuevaCuentaUsuario) {

        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
            .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idUsuario + " no fue encontrado en la BD."));

    // Verificar si el usuario ya tiene una cuenta de cliente y una de administrador
    boolean tieneCliente = false;
    boolean tieneAdministrador = false;
    for (CuentaUsuario cuenta : usuario.getCuentas()) {
        if (cuenta.getTipousuario() == TipoUsuario.cliente) {
            tieneCliente = true;
        } else if (cuenta.getTipousuario() == TipoUsuario.administrador) {
            tieneAdministrador = true;
        }
    }

    if (tieneCliente && tieneAdministrador) {
        throw new RecursoNoEncontradoException("Error: El usuario ya tiene una cuenta de cliente y una de administrador.");
    }

    // Si no se viola el index unique de username, agregar la nueva cuenta de usuario
    usuario.getCuentas().add(nuevaCuentaUsuario);

    try {
        // Guardar el usuario en la base de datos
        usuariosRepository.save(usuario);
    } catch (DataIntegrityViolationException e) {
        // Manejar la excepción en caso de violación de integridad
        throw new RecursoNoEncontradoException("Error: El username " +nuevaCuentaUsuario.getUsername()+" ya está en uso.");
    }
    }
    


    @Override
    public void eliminarCuentaUsuario(int idUsuario, String username) {

        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error! El usuario con el ID " + idUsuario + " no fue encontrado en la BD."));


        List<CuentaUsuario> cuentas = usuario.getCuentas();

        
        // buscar la cuenta con el nombre de usuario proporcionado
        Optional<CuentaUsuario> cuentaOptional = cuentas.stream()
                .filter(cuenta -> cuenta.getUsername().equals(username))
                .findFirst();

        // verificar si se encontró la cuenta
        if (cuentaOptional.isPresent()) {
            // eliminar la cuenta del usuario
            cuentas.remove(cuentaOptional.get());

            usuariosRepository.save(usuario);
        } else {
            throw new RecursoNoEncontradoException("Error! La cuenta con el username '" + username + "' no existe para el usuario con ID " + idUsuario);
        }
    }


    @Override
    public void actualizarCuentaUsuario(int idUsuario, String username, CuentaUsuario nuevaCuentaUsuario) {
        UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error! El usuario con el ID " + idUsuario + " no fue encontrado en la BD."));
    
        List<CuentaUsuario> cuentas = usuario.getCuentas();
    
        // Buscar la cuenta en la lista por su nombre de usuario
        Optional<CuentaUsuario> cuentaOptional = cuentas.stream()
                .filter(cuenta -> cuenta.getUsername().equals(username))
                .findFirst();
    
        // Verificar si la cuenta existe
        if (cuentaOptional.isPresent()) {
            // Obtener la cuenta existente
            CuentaUsuario cuentaExistente = cuentaOptional.get();
    
            // Actualizar los datos de la cuenta con los nuevos valores
            if (nuevaCuentaUsuario.getUsername() != null && !nuevaCuentaUsuario.getUsername().isEmpty()) {
                cuentaExistente.setUsername(nuevaCuentaUsuario.getUsername());
            }
            if (nuevaCuentaUsuario.getContrasena() != null && !nuevaCuentaUsuario.getContrasena().isEmpty()) {
                cuentaExistente.setContrasena(nuevaCuentaUsuario.getContrasena());
            }
        } else {
            throw new RecursoNoEncontradoException("Error! La cuenta con el username " + username + " no existe para el usuario con ID " + idUsuario);
        }
    
        // Guardar el usuario actualizado en la base de datos
        try {
            usuariosRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            // Se viola el índice único del nombre de usuario
            throw new RecursoNoEncontradoException("El username " + nuevaCuentaUsuario.getUsername() + " ya está en uso.");
        }

    
    }


    @Override
    public void actualizarInformacionContacto(int idUsuario, String nuevoEmail) {

        Optional<UsuariosModel> usuarioOptional = usuariosRepository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            UsuariosModel usuario = usuarioOptional.get();
            // Limpiar cualquier espacio en blanco alrededor del email
            nuevoEmail = nuevoEmail.trim();
            usuario.setEmail(nuevoEmail);
            try {
                usuariosRepository.save(usuario);
            } catch (DataIntegrityViolationException e) {
                if (e.getMessage().contains("email")) {
                    throw new DuplicateKeyException("El correo electrónico " + nuevoEmail + " ya está en uso.");
                } else {
                    throw e; // Re-throw DataIntegrityViolationException for other cases
                }
            }
        } else {
            throw new RecursoNoEncontradoException("El usuario con el ID " + idUsuario + " no fue encontrado en la base de datos.");
        }
    }
}