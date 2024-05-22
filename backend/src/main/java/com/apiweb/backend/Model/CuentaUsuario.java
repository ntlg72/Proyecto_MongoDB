package com.apiweb.backend.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.apiweb.backend.Model.ENUM.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CuentaUsuario {
    private TipoUsuario tipousuario;
    private String username;
    private String contrasena;
}
