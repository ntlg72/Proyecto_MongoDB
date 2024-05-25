package com.apiweb.backend.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.apiweb.backend.Model.ENUM.Genero;
import com.apiweb.backend.Model.ENUM.Tipo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Productos")
@TypeAlias("productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductosModel {
    @Id
    private Integer id;
    private String nombre;
    private double precio;
    private String descripcion;
    private boolean paquete;
    private Genero genero;
    private String imagenURL;
    private Tipo tipo;
    private String categoria;
    private List<Talla> talla = new ArrayList<>();
    private List<Comentario> comentarios = new ArrayList<>();
    private List<ProductosPaquete> productospaquete = new ArrayList<>();
}