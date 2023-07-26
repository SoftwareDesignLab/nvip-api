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
    @NonNull @Id @Column(name="affected_product_id")
    private int affectedProductId;

    @NonNull @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    @Column(name="cpe", columnDefinition="VARCHAR(300)", nullable=false)
    private String cpe;

    @Basic @Column(name="release_date", nullable=false) 
    private LocalDateTime releaseDate;

    @Column(name="product_name", columnDefinition="tinytext", nullable=false)
    private String productName;

    @Column(name="version", columnDefinition="tinytext", nullable=false)
    private String version;

    @Column(name="vendor", columnDefinition="tinytext", nullable=false)
    private String vendor;

    @Column(name="purl", columnDefinition="tinytext", nullable=false)
    private String purl;
}
