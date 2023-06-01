package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class VdoCharacteristicDTO {
    String cveId;
    String vdoLabel;
    double vdoConfidence;
    String vdoNounGroup;
}
