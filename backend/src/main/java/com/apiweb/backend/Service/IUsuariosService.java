package com.apiweb.backend.Service;

import java.util.List;

import com.apiweb.backend.Model.CuentaUsuario;
import com.apiweb.backend.Model.UsuariosModel;

public interface IUsuariosService {
    //guardar usuario)
    String guardarUsuario (UsuariosModel usuario);
    //buscar usuario por id
    UsuariosModel buscarUsuarioPorId(int idUsuario);
    //listar usuarios
    List<UsuariosModel> listarUsuarios();
    //eliminar usuario por id
    void eliminarUsuarioPorId(int idUsuario);
    //listar las cuentas de un usuario por id
    List<CuentaUsuario> cuentasUsuarios (int idUsuario);
    //agregar cuenta a un usuario
    void agregarCuentaUsuario (int idUsuario, CuentaUsuario nuevaCuentaUsuario);
    //eliminar cuenta de un usuario po username (por ej, eliminar cuenta de cliente y dejar la de admin)
    void eliminarCuentaUsuario (int idUsuario, String username);
    //actualizar username o contrasena de cuenta
    public void actualizarCuentaUsuario(int idUsuario, String username, CuentaUsuario nuevaCuentaUsuario);
    //actualizar email
    public void actualizarInformacionContacto(int idUsuario, String nuevoEmail);
}