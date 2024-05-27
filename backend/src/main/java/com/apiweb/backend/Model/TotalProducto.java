 package com.apiweb.backend.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
 @Document
 @AllArgsConstructor
 @NoArgsConstructor
 public class TotalProducto {
    private String idProducto;
    private String nombreProducto;
    private int totalCantidad;
    
 }
 