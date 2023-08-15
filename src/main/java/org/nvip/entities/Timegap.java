package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="timegap")
@Getter
@Setter
@ToString
public class Timegap {
    @Id
    private int timegapId;

    @ManyToOne @JoinColumn(name = "cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    // Only possible values are: "nvd" or "mitre"
    private String location;

    // Measured in hours
    private double timegap;

    @Basic private LocalDateTime createdDate;
}
