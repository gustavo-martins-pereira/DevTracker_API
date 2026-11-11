package com.gustavomp.devtrackerapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "fixedprice_projects")
@Getter
@Setter
public class FixedPriceProject extends Project {

    private BigDecimal budget;

}
