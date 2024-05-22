package com.apiweb.backend.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.apiweb.backend.Model.ENUM.Valoracion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Comentario {
    private String comentarios;
    private Valoracion valoracion;
    private Integer idusuario;
}
