package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class AffectedRelease {
    @Id @Column(name="affected_product_id") private int id;

    @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    private String cpe;
    @Basic private LocalDateTime releaseDate;
    private String productName;
    private String version;
    private String vendor;
    private String purl;
}
