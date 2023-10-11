package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
@Builder
public class ChartsDTO {
    // what the home page charts expect
    // can probably make these lists at some point instead of
    // semi-colon separated strings
    private String not_in_mitre_count;
    private String not_in_nvd_count;
    private String cvesUpdated;
    private String cvesAdded;
    private String avgTimeGapNvd;
    private String avgTimeGapMitre;
    private String run_date_times;

}
