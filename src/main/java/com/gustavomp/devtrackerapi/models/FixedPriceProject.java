package com.gustavomp.devtrackerapi.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class FixedPriceProject extends Project {

    private BigDecimal budget;

}
