package com.apiweb.backend.Model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import com.apiweb.backend.Model.ENUM.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Pago {
    private MetodoPago metodopago;
    private Date fechapago;
    private double valorpago;
    private boolean pagado;
}