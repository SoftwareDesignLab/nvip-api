package org.nvip.api.services;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.*;
import org.nvip.data.repositories.VulnRepository;
import org.nvip.entities.*;
import org.nvip.util.AppException;
import org.nvip.util.CvssGenUtil;
import org.nvip.util.VulnerabilityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VulnService {

    final VulnRepository vulnRepository;
    final VulnerabilityUtil vulnerabilityUtil;
    final CvssGenUtil cvssGenUtil;

    VulnerabilityDTO toDTO(Vulnerability v) {
        if (v == null) return null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        VulnerabilityDTO.VulnerabilityDTOBuilder builder = VulnerabilityDTO.builder();

        Description desc = v.getDescription();

        builder.vulnId(v.getVulnId())
                .cveId(v.getCveId())
                .description(desc.getDescription())
                .existInMitre(v.existsInMitre())
                .existInNvd(v.existsInNvd())
                .type(parseType(v.getVdoCharacteristics()));

        // if description is system generated, try to find a user generated altDesc
        // if description is user generated, definitely find the most recent system generated altDesc
        Description altDescription = vulnerabilityUtil.getAltDescription(v.getDescription());
        if (altDescription == null) // case where there's no user desc
            builder.description(v.getDescriptionString());
        else if (altDescription.getIsUserGenerated() == 1) { // case where user desc exists, this should trump system desc
            builder.description(altDescription.getDescription());
            builder.altDescription(DescriptionDTO.builder()
                    .description(v.getDescriptionString())
                    .createdDate(v.getDescription().getCreatedDate().format(dateFormatter))
                    .isUserGenerated(v.getDescription().getIsUserGenerated())
                    .build());
        }
        else { // case where alt desc is system generated, main desc is user generated, no need to switch anything
            builder.description(v.getDescriptionString());
            builder.altDescription(DescriptionDTO.builder()
                    .description(altDescription.getDescription())
                    .createdDate(altDescription.getCreatedDate().format(dateFormatter))
                    .isUserGenerated(altDescription.getIsUserGenerated())
                    .build());
        }

        if(v.getPublishedDate() != null){
            builder.publishedDate(v.getPublishedDate().format(dateTimeFormatter));
        } else builder.publishedDate("N/A");

        if(v.getLastModifiedDate() != null){
            builder.lastModifiedDate(v.getLastModifiedDate().format(dateTimeFormatter));
        } else builder.lastModifiedDate("N/A");

        if(v.getCreatedDate() != null){
            builder.createdDate(v.getCreatedDate().format(dateFormatter));
        }

        List<VdoCharacteristic> characteristics = v.getVdoCharacteristics();
        // filter VDO labels by vdoLabel, max by createdDate
        characteristics = characteristics.stream()
                .collect(Collectors.groupingBy(VdoCharacteristic::getVdoLabel))
                .values()
                .stream()
                .map(x -> x.stream().max(Comparator.comparing(VdoCharacteristic::getCreatedDate)).get())
                .collect(Collectors.toList());

        for(VdoCharacteristic characteristic: characteristics){
            builder.characteristic(VdoCharacteristicDTO.builder()
                    .cveId(v.getCveId())
                    .vdoLabel(characteristic.getVdoLabel())
                    .vdoConfidence(characteristic.getVdoConfidence())
                    .vdoNounGroup(characteristic.getVdoNounGroup())
                    .userId(characteristic.getUserId())
                    .isActive(characteristic.getIsActive())
                    .build());
        }
//        builder.type(parseType(v.getVdoCharacteristics()));
//TODO: we dont need cve id in these objects
        for(RawDescription rawDesc: v.getRawDescriptions()){
            builder.source(
                    RawDescriptionDTO.builder()
                            .cveId(v.getCveId())
                            .rawDescription(rawDesc.getRawDescription())
                            .createdDate(String.valueOf(rawDesc.getCreatedDate()))
                            .publishedDate(String.valueOf(rawDesc.getPublishedDate()))
                            .lastModifiedDate(String.valueOf(rawDesc.getLastModifiedDate()))
                            .sourceUrl(rawDesc.getSourceUrl())
                            .isGarbage(rawDesc.getIsGarbage())
                            .sourceType(rawDesc.getSourceType())
                            .parserType(rawDesc.getParserType())
                            .build()
            );
        }

        List<String> cpes = new ArrayList<>();
        for(AffectedProduct product: v.getAffectedProducts()){
            builder.product(ProductDTO.builder()
                    .productId(product.getAffectedProductId())
                    .productName(product.getProductName())
                    .cpe(product.getCpe())
                    .version(product.getVersion())
                    .build());
            builder.cpe(product.getCpe());
            cpes.add(product.getCpe());
        }
        builder.domain(parseDomain(cpes));

        builder.introducedDate("N/A");
        builder.fixedDate("N/A");

        // TODO: this ultimately replaces all previous CVSS storing using a simple lookup
        // get rid of old CVSS table and code when ready
        // get rid of CVSS insertions
        Double calculatedCVSSScore = cvssGenUtil.calculateCVSSScore(characteristics);

        if (calculatedCVSSScore != null) {
            builder.cvssScore(CvssDTO.builder()
                    .cveId(v.getCveId())
                    .baseScore(calculatedCVSSScore)
                    .build());

            for (PatchCommit patch : v.getPatchCommits()) {
                builder.patchCommit(
                    PatchCommitDTO.builder()
                        .cveId(v.getCveId())
                        .sourceUrl(patch.getSourceUrl().getSourceUrl())
                        .commitSha(patch.getCommitSha())
                        .commitMessage(patch.getCommitMessage())
                        .commitDate(String.valueOf(patch.getCommitDate()))
                        .linesChanged(patch.getLinesChanged())
                        .build()
                );
            }

            for (Fix fix : v.getFixes()) {
                builder.fix(
                    FixDTO.builder()
                        .cveId(v.getCveId())
                        .sourceUrl(fix.getSourceUrl())
                        .fixDescription(fix.getFixDescription())
                        .build()
                );
            }

            for (Exploit exploit : v.getExploits()) {
                builder.exploit(
                    ExploitDTO.builder()
                        .cveId(v.getCveId())
                        .source(exploit.getSource())
                        .sourceUrl(exploit.getSourceUrl())
                        .description(exploit.getDescription())
                        .dateCreated(String.valueOf(exploit.getDateCreated()))
                        .datePublished(String.valueOf(exploit.getDatePublished()))
                        .isRepo(exploit.isRepo())
                        .build()
                );
            }
        }

        return builder.build();
    }

    List<VulnerabilityDTO> toDTO(List<Vulnerability> vulnerabilities) {
        return vulnerabilities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VulnerabilityDTO> searchVulns(String keyword, LocalDateTime startDate, LocalDateTime endDate, double[] cvssScores, String[] vdoLabels, Integer limitCount, String product) {
        List<Vulnerability> vulnerabilities = vulnRepository.searchVulnerabilities(keyword, startDate, endDate, cvssScores, vdoLabels, limitCount, product);
        return toDTO(vulnerabilities);
    }

    public List<VulnerabilityDTO> findByCreatedDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Vulnerability> vulnerabilities = vulnRepository.findByCreatedDate(startDate, endDate);
        return toDTO(vulnerabilities.stream().filter(this::removeLowQualityCve).collect(Collectors.toList()));
    }

    public VulnerabilityDTO getVulnerability(String cveId) {
        Vulnerability vulnerability = vulnRepository.findByCveId(cveId)
                .orElseThrow(() -> new AppException("Vulnerability not found with id: " + cveId, HttpStatus.NOT_FOUND));
        return toDTO(vulnerability);
    }


    String parseDomain(List<String> cpes) {
        String domain = "N/A";
        if (cpes != null && !cpes.isEmpty()) {
            domain = VulnerabilityUtil.getCompanyProduct(cpes.get(0));
        }
        return domain;
    }

    String parseType(List<VdoCharacteristic> vdoCharacteristics){
        String type = vdoCharacteristics.stream()
                .map(VdoCharacteristic::getVdoNounGroup)
                .filter(vdoNounGroup -> vdoNounGroup.contains("IMPACT_METHOD"))
                .distinct()
                .collect(Collectors.joining(", "));
        return type.isEmpty() ? "N/A" : type;
    }

    private boolean removeLowQualityCve(Vulnerability vuln) {
        String pattern = "\\bCVE-20\\d{2}-\\d{4,5}\\b";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(vuln.getDescriptionString());

        int patternLength = 0;

        while (m.find())
            patternLength += m.group().length();

        return !(patternLength >= vuln.getDescriptionString().length() * 0.9);
    }

}
