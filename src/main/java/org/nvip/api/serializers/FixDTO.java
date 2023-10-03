package org.nvip.api.serializers;

import lombok.*;

@Getter
@Setter
@Builder
public class FixDTO {
    String cveId;
    String sourceUrl;
    String fixDescription;
}
