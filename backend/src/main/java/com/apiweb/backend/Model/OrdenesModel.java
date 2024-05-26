package com.apiweb.backend.Model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Ordenes")
@TypeAlias("ordenes")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrdenesModel {
    @Id
    private Integer id;
    private Integer idusuario;
    private double valortotal;
    private Date fechaorden;
    private Pago pago;
    private List<Contiene> contiene = new ArrayList<>();
}
