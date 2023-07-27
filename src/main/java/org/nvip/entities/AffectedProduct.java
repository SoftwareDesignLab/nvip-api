package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name="affectedproduct", schema = "nvip")
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class AffectedProduct {
    @Id
    private int affectedProductId;

    @NonNull @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    @Basic private LocalDateTime releaseDate;

    private String cpe;

    private String productName;

    private String version;

    private String vendor;

    private String purl;

    private String swidTag;
}
