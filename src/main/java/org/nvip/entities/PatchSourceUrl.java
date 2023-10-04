package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="patchsourceurl")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PatchSourceUrl {
    @Id private int sourceUrlId;

    @OneToOne @JoinColumn(name = "source_url_id")
    private PatchCommit patchCommit;

    private String sourceUrl;
}
