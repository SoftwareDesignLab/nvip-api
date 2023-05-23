package org.nvip.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
@Table(name="affectedrelease")
public class AffectedRelease {
    @NonNull @Id Integer id;

    @NonNull Integer version;

    @NonNull @ManyToOne @JoinColumn(name="cve_id")
    Vulnerability vulnerability;

    @NonNull @OneToOne @JoinColumn(name="product_id")
    Product product;
}
