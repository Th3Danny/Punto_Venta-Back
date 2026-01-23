package com.punto_venta.persistance.entities;

import jakarta.persistence.*;

@Entity
@Table (name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nombre; // ADMIN, CAJERO, GERENTE
}
