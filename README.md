# Sportwear E - Commerce Database Services

Este proyecto tiene como objetivo principal el modelado e implementación de un sistema de almacenamiento destinado a respaldar el desarrollo de una aplicación web para una tienda online especializada en la venta y personalización de ropa deportiva. 


# Implementación

La implementación de la solución del sistema de almacenamiento abarca por una parte el uso de una  base de datos NoSQL (MongoDB), y el uso de Spring Boot y Java, con el fin de asegurar una gestión eficiente de la información y el funcionamiento óptimo de la aplicación web. 

## Modelado

El modelado de la base de datos se realizó a partir de 5 colecciones: Usuarios, Productos, Promociones, Ordenes y Envíos. La colecciones con sus respectivos documentos y campos se pueden evidenciar en la siguiente imagen.

## Esquema de las colecciones

    use Proyecto;
    
    db.createCollection("Promociones", {
        "capped": false,
        "validator": {
            "$jsonSchema": {
                "bsonType": "object",
                "title": "Promociones",
                "properties": {
                    "_id": {
                        "bsonType": "number"
                    },
                    "fechainicio": {
                        "bsonType": "date"
                    },
                    "fechafin": {
                        "bsonType": "date"
                    },
                    "descuento": {
                        "bsonType": "double"
                    },
                    "productos": {
                        "bsonType": "array",
                        "additionalItems": true,
                        "items": {
                            "bsonType": "object",
                            "additionalProperties": true,
                            "patternProperties": {
                                "idproductos": {
                                    "bsonType": "number"
                                }
                            }
                        }
                    }
                },
                "additionalProperties": true,
                "required": [
                    "_id",
                    "fechainicio",
                    "fechafin",
                    "descuento",
                    "productos"
                ]
            }
        },
        "validationLevel": "moderate",
        "validationAction": "warn"
    });
    
    
    
    
    db.createCollection("Usuarios", {
        "capped": false,
        "validator": {
            "$jsonSchema": {
                "bsonType": "object",
                "title": "Usuarios",
                "properties": {
                    "_id": {
                        "bsonType": "number"
                    },
                    "nombre": {
                        "bsonType": "string"
                    },
                    "identificacion": {
                        "bsonType": "string"
                    },
                    "email": {
                        "bsonType": "string"
                    },
                    "cuentas": {
                        "bsonType": "array",
                        "additionalItems": true,
                        "items": {
                            "bsonType": "object",
                            "properties": {
                                "username": {
                                    "bsonType": "string"
                                },
                                "contrasena": {
                                    "bsonType": "string"
                                }
                            },
                            "additionalProperties": true,
                            "patternProperties": {
                                "tipousuario": {
                                    "bsonType": "string",
                                    "enum": [
                                        "cliente",
                                        "administrador"
                                    ]
                                }
                            }
                        }
                    }
                },
                "additionalProperties": true,
                "required": [
                    "_id",
                    "nombre",
                    "identificacion",
                    "email"
                ]
            }
        },
        "validationLevel": "moderate",
        "validationAction": "warn"
    });
    
    db.Usuarios.createIndex({
        "email": 1
    },
    {
        "name": "Index_Email",
        "unique": true
    });
    
    db.Usuarios.createIndex({
        "cuentas.username": 1
    },
    {
        "name": "Index_Username",
        "unique": true
    });
    
    db.Usuarios.createIndex({
        "identificacion": 1
    },
    {
        "name": "Index_Identificacion",
        "unique": true
    });



db.createCollection("Envios", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Envios",
            "properties": {
                "_id": {
                    "bsonType": "number"
                },
                "idorden": {
                    "bsonType": "number"
                },
                "direccion": {
                    "bsonType": "string"
                },
                "valorenvio": {
                    "bsonType": "double"
                },
                "barrio": {
                    "bsonType": "string"
                },
                "detallesubicacion ": {
                    "bsonType": "object",
                    "additionalProperties": true,
                    "patternProperties": {
                        "ciudad": {
                            "bsonType": "string"
                        },
                        "departamento": {
                            "bsonType": "string"
                        },
                        "codigopostal": {
                            "bsonType": "number"
                        }
                    }
                }
            },
            "additionalProperties": true,
            "required": [
                "_id",
                "idorden",
                "direccion",
                "valorenvio",
                "detallesubicacion "
            ]
        }
    },
    "validationLevel": "moderate",
    "validationAction": "warn"
});




db.createCollection("Ordenes", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Ordenes",
            "properties": {
                "_id": {
                    "bsonType": "number"
                },
                "idusuario": {
                    "bsonType": "number"
                },
                "subtotal": {
                    "bsonType": "double"
                },
                "fechaorden": {
                    "bsonType": "date"
                },
                "pago": {
                    "bsonType": "object",
                    "additionalProperties": true,
                    "patternProperties": {
                        "metodopago": {
                            "bsonType": "string",
                            "enum": [
                                "pse",
                                "efecty",
                                "contraentrega",
                                "addi"
                            ]
                        },
                        "fechapago": {
                            "bsonType": "date"
                        },
                        "valorpago": {
                            "bsonType": "double"
                        },
                        "pagado": {
                            "bsonType": "bool"
                        }
                    }
                },
                "contiene": {
                    "bsonType": "array",
                    "additionalItems": true,
                    "items": {
                        "bsonType": "object",
                        "additionalProperties": true,
                        "patternProperties": {
                            "idproducto": {
                                "bsonType": "number"
                            },
                            "cantidad": {
                                "bsonType": "number"
                            },
                            "talla": {
                                "bsonType": "string"
                            }
                        }
                    }
                }
            },
            "additionalProperties": true,
            "required": [
                "_id",
                "idusuario",
                "subtotal",
                "fechaorden",
                "pago"
            ]
        }
    },
    "validationLevel": "moderate",
    "validationAction": "warn"
});




db.createCollection("Productos", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Productos",
            "properties": {
                "_id": {
                    "bsonType": "number"
                },
                "nombre": {
                    "bsonType": "string"
                },
                "precio": {
                    "bsonType": "double"
                },
                "descripcion": {
                    "bsonType": "string"
                },
                "imagenRef": {
                    "bsonType": "string"
                },
                "paquete": {
                    "bsonType": "bool"
                },
                "genero": {
                    "bsonType": "string",
                    "enum": [
                        "Hombre",
                        "Mujer",
                        "Ninos"
                    ]
                },
                "tipo": {
                    "bsonType": "string",
                    "enum": [
                        "paquete",
                        "individual"
                    ]
                },
                "imagenURL": {
                    "bsonType": "string"
                },
                "categoria": {
                    "bsonType": "string"
                },
                "talla": {
                    "bsonType": "array",
                    "additionalItems": true,
                    "items": {
                        "bsonType": "object",
                        "additionalProperties": false,
                        "patternProperties": {
                            "nombre": {
                                "bsonType": "string"
                            },
                            "cantidad": {
                                "bsonType": "number"
                            }
                        }
                    }
                },
                "productospaquete": {
                    "bsonType": "array",
                    "additionalItems": true,
                    "items": {
                        "bsonType": "object",
                        "additionalProperties": true,
                        "patternProperties": {
                            "idproducto": {
                                "bsonType": "number"
                            }
                        }
                    }
                },
                "comentario": {
                    "bsonType": "array",
                    "additionalItems": true,
                    "items": {
                        "bsonType": "object",
                        "additionalProperties": true,
                        "patternProperties": {
                            "comentarios": {
                                "bsonType": "string"
                            },
                            "valoracion": {
                                "bsonType": "string",
                                "enum": [
                                    "uno",
                                    "dos",
                                    "tres",
                                    "cuatro",
                                    "cinco"
                                ]
                            },
                            "idusuario": {
                                "bsonType": "number"
                            }
                        }
                    }
                }
            },
            "additionalProperties": true,
            "required": [
                "_id",
                "nombre",
                "precio",
                "imagenRef",
                "paquete",
                "tipo",
                "categoria",
                "talla"
            ]
        }
    },
    "validationLevel": "moderate",
    "validationAction": "warn"
});


















![Modelado-1](https://github.com/ntlg72/Proyecto_MongoDB/assets/166937483/bbab5781-d52a-4eec-a6a1-064750f52d68)
		
