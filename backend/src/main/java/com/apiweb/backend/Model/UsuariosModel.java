package com.apiweb.backend.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Usuarios")
@TypeAlias ("usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuariosModel {
    @Id
    private long idUsuario;
    private String identificacion;
    private String nombre;
    private String email;
    //arreglo de documentos incrustados de Cuentas
    private List<CuentaUsuario> cuentas = new ArrayList<>();

}