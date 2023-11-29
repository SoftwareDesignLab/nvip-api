package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name="affectedproduct")
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class AffectedProduct {
    @Id
    private int affectedProductId;

    @NonNull @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="cpe_set_id", referencedColumnName = "cpeSetId")
    private CpeSet cpeSet;

    private String cpe;

    private String productName;

    private String version;

    private String vendor;

    private String purl;

    private String swidTag;
}
