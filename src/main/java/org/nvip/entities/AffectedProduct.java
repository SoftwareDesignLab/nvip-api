package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name="affectedproduct")
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class AffectedProduct {
    @NonNull @Id
    private int affectedProductId;

    @NonNull @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    @Column(columnDefinition="VARCHAR(300)", nullable=false)
    private String cpe;

    @Basic @Column(nullable=false) 
    private LocalDateTime releaseDate;

    @Column(columnDefinition="tinytext", nullable=false)
    private String productName;

    @Column(columnDefinition="tinytext", nullable=false)
    private String version;

    @Column(columnDefinition="tinytext", nullable=false)
    private String vendor;

    @Column(columnDefinition="tinytext", nullable=false)
    private String purl;
}
