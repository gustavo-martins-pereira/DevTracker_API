package com.gustavomp.devtrackerapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "hourlyrate_projects")
@Getter
@Setter
public class HourlyRateProject extends Project {

    private BigDecimal hourlyRate;

}
