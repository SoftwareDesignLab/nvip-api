package org.nvip.api.services;


import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.VulnerabilitySummaryDTO;
import org.nvip.entities.VdoCharacteristic;
import org.nvip.entities.Vulnerability;
import org.nvip.entities.VulnerabilityAggregate;
import org.nvip.util.VulnerabilityUtil;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VulnService {

    //TODO: should be priv eventually
    public String parseDomain(List<String> cpes) {
        String domain = "N/A";
        if (cpes != null && cpes.size() > 0) {
            domain = VulnerabilityUtil.getCompanyProduct(cpes.get(0));
        }
        return domain;
    }

    //TODO: should be priv eventually
    public String parseType(List<VdoCharacteristic> vdoCharacteristics){
        String type = vdoCharacteristics.stream()
                .filter(x -> x.getVdoNounGroup().contains("IMPACT_METHOD"))
                .map(x -> x.getVdoNounGroup())
                .distinct()
                .collect(Collectors.joining(", "));
        return type.isEmpty() ? "N/A" : type;
    }

    public VulnerabilitySummaryDTO mapVuln(Vulnerability vuln) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        VulnerabilitySummaryDTO.VulnerabilitySummaryDTOBuilder builder = VulnerabilitySummaryDTO.builder();
        builder.vulnId(vuln.getVulnId())
                .cveId(vuln.getCveId())
                .description(vuln.getDescriptionString().length() < 297 ? vuln.getDescriptionString() : vuln.getDescriptionString().substring(0, 297)+"...")
                .publishedDate(
                        vuln.getPublishedDate() != null ? vuln.getPublishedDate().format(dateTimeFormatter) : "N/A"
                )
                .lastModifiedDate(
                        vuln.getLastModifiedDate() != null ? vuln.getLastModifiedDate().format(dateTimeFormatter) : "N/A")
                .createdDate(vuln.getCreatedDate().format(dateFormatter))
                .existInNvd(vuln.existsInNvd())
                .existInMitre(vuln.existsInMitre())
        ;

        return builder.build();
    }

    public boolean removeLowQualityCve(VulnerabilityAggregate vuln) {
        String pattern = "\\bCVE-20\\d{2}-\\d{4,5}\\b";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(vuln.getDescription());;

        int patternLength = 0;

        while (m.find())
            patternLength += m.group().length();

        return !(patternLength >= vuln.getDescription().length() * 0.9);
    }
}
