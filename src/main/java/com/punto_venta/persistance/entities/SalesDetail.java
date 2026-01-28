package com.punto_venta.persistance.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_detail")
@Data
public class SalesDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sales sale;   

    @ManyToOne
    private Products product;

    private int amount;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

    private BigDecimal iva;




}
