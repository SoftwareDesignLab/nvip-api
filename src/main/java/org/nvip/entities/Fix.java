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
    private String sourceUrl; // This will be a direct field, not a join on fixsourceurl, as it was found to be redundant
//    private String fixType;
}
