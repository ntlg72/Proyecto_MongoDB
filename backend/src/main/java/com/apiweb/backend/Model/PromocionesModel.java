package com.apiweb.backend.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Promociones")
@TypeAlias("promociones")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PromocionesModel{
    @Id
    private Integer id;
    private Date fechainicio;
    private Date fechafin;
    private Integer descuento;
    private List<ProductoPromocion> productos = new ArrayList<>();

}