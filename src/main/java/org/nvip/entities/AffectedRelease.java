package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
@Table(name="affectedrelease")
@Getter
@Setter
public class AffectedRelease {
    @NonNull @Id Integer id;

    String version;

    @NonNull @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    Vulnerability vulnerability;

    @NonNull @OneToOne(fetch = FetchType.EAGER) @JoinColumn(name="product_id")
    Product product;
}
