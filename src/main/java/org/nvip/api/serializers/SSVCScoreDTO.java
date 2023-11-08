package org.nvip.api.serializers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SSVCScoreDTO {
    @JsonProperty("ssvcScoreLow")
    private String scoreLow;
    @JsonProperty("ssvcScoreMedium")
    private String scoreMedium;
    @JsonProperty("ssvcScoreHigh")
    private String scoreHigh;

    public String get(int i) {
        switch (i) {
            case 0:
                return scoreLow;
            case 1:
                return scoreMedium;
            case 2:
                return scoreHigh;
            default:
                return "INVALID VALUE";
        }
    }
}
