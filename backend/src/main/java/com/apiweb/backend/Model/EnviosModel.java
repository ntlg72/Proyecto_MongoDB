package com.apiweb.backend.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Envios")
@TypeAlias ("envios")
@Data
@AllArgsConstructor
@NoArgsConstructor


public class EnviosModel {
    @Id
    private Integer idenvio;
    private Integer idorden;
    private String direccion;
    private double valorenvio;
    private String barrio;
    private String infoadicional;
    private DetallesUbicacion detallesubicacion;
}
