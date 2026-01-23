package com.punto_venta.persistance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @ManyToOne
    private Users atm;

    private BigDecimal total;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SalesDetail> detalles;
}
