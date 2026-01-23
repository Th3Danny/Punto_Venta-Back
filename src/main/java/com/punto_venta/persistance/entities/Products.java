package com.punto_venta.persistance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table (name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean activo = true;
}
