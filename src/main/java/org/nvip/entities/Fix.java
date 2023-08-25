package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="fixes")
@Getter
@Setter
@ToString
public class Fix {
    @Id private int fixId;
    @ManyToOne @JoinColumn(name="cve_id", referencedColumnName="cveId")
    private Vulnerability vulnerability;
    private String fixDescription;
    private String sourceUrl;
//    private String fixType;
}
