package org.nvip.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nvip.entities.Cvss;
import org.nvip.entities.VdoCharacteristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CvssGenUtil {

    private final ApplicationContext ctx;

    private static final Logger logger = LogManager.getLogger(CvssGenUtil.class);

    public CvssGenUtil(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public Double calculateCVSSScore(List<VdoCharacteristic> vdoCharacteristics) {
        // use set of all vulns VDO labels to generate a
        // partial CVSS vector
        Set<VDOLabel> predictionsForVuln = mapToLabelSet(vdoCharacteristics);
        String[] cvssVector = getCvssVector(predictionsForVuln);
        // use csv map to grab new CVSS score
        Map<CVSSVector, Double> scoreTable = loadScoreTable();
        Double score = scoreTable.get(new CVSSVector(cvssVector));
        // return new CVSS with this score
        return score;
    }

    private Set<VDOLabel> mapToLabelSet(List<VdoCharacteristic> vdoCharacteristics) {
        return vdoCharacteristics.stream().peek(v -> logger.info(v.getVdoLabel())).map(v->VDOLabel.getVdoLabel(v.getVdoLabel())).collect(Collectors.toSet());
    }

    /**
     * A partial CVSS vector is a list like ["P", "X", "X", "X", "X", "H", "H",
     * "H"], where each item in the list represents the values of AV, AC, PR, UI, S,
     * C, I, A, respectively
     *
     * AV: Attack Vector, AC: Attack Complexity, PR: Privilege Required, S: Scope,
     * UI: User Interaction, C: Confidentiality, I: Integrity, A: Availability.
     *
     * Note: Right now we do not have any mapping for: PR, UI, S fields of the CVSS
     * vector
     *
     * @param predictionsForVuln: Predictions for each VDO noun group. The value of
     *                            the map is ArrayList<String[]> to store the label
     *                            and confidence for each noun group value.
     * @return
     */
     private String[] getCvssVector(Set<VDOLabel> predictionsForVuln) {

        // values for: AV, AC, PR, UI, S, C, I, A
        // initially set to unknown
        String[] vectorCvss = new String[] { "X", "L", "X", "X", "U", "N", "N", "N" };
        Map<VDONounGroup, Set<VDOLabel>> nounToLabels = predictionsForVuln.stream().collect(Collectors.groupingBy(v->v.vdoNounGroup, Collectors.toSet()));

        for (VDONounGroup vdoNounGroup : nounToLabels.keySet()) {
            Set<VDOLabel> predictionsForNounGroup = nounToLabels.get(vdoNounGroup);
            // attack theater
            if (vdoNounGroup == VDONounGroup.ATTACK_THEATER) {
                /**
                 * Attack Vector (AV)* Network (AV:N), Adjacent (AV:A), Local (AV:L), Physical
                 * (AV:P)
                 *
                 */
                if (predictionsForNounGroup.contains(VDOLabel.REMOTE))
                    vectorCvss[0] = "N";
                else if (predictionsForNounGroup.contains(VDOLabel.LIMITED_RMT))
                    vectorCvss[0] = "N";
                else if (predictionsForNounGroup.contains(VDOLabel.LOCAL))
                    vectorCvss[0] = "L";
                else if (predictionsForNounGroup.contains(VDOLabel.PHYSICAL))
                    vectorCvss[0] = "P";

            } else if (vdoNounGroup == VDONounGroup.CONTEXT) {
                // no mapping yet
            } else if (vdoNounGroup == VDONounGroup.IMPACT_METHOD) {
                /**
                 * Attack Complexity (AC)* Low (AC:L)High (AC:H)
                 *
                 */
                if (predictionsForNounGroup.contains(VDOLabel.MAN_IN_THE_MIDDLE))
                    vectorCvss[1] = "H"; // if there is MitM impact then, we assume attack complexity is High
                else if (predictionsForNounGroup.contains(VDOLabel.CONTEXT_ESCAPE))
                    vectorCvss[4] = "C"; // scope changes if context escape

            } else if (vdoNounGroup == VDONounGroup.LOGICAL_IMPACT) {

                /**
                 * ******************* CONFIDENTIALITY **************************
                 *
                 * (Privilege Escalation && (len(Logical Impact)==1 || Read || Indirect
                 * Disclosure)) -> C: H
                 *
                 * Read || Indirect Disclosure-> C: LH
                 *
                 * ******************* INTEGRITY **************************
                 *
                 * (Privilege Escalation && (len(Logical Impact)==1) || Write || Resource
                 * Removal)) -> I: H
                 *
                 * Write || Resource Removal -> I: LH
                 *
                 *
                 * ******************* AVAILABILITY **************************
                 *
                 * (Privilege Escalation && (len(Logical Impact)==1 || Service Interrupt)) -> A:
                 * H
                 *
                 * Service Interrupt -> A:LH
                 *
                 */
                if (predictionsForNounGroup.contains(VDOLabel.PRIVILEGE_ESCALATION)
                        && (predictionsForNounGroup.size() == 1 || predictionsForNounGroup.contains(VDOLabel.READ) || predictionsForNounGroup.contains(VDOLabel.INDIRECT_DISCLOSURE))

                )
                    vectorCvss[5] = "H"; // confidentiality H
                else if (predictionsForNounGroup.contains(VDOLabel.PRIVILEGE_ESCALATION)
                        && (predictionsForNounGroup.size() == 1 || predictionsForNounGroup.contains(VDOLabel.WRITE) || predictionsForNounGroup.contains(VDOLabel.RESOURCE_REMOVAL))

                )
                    vectorCvss[6] = "H"; // integrity H
                else if (predictionsForNounGroup.contains(VDOLabel.PRIVILEGE_ESCALATION) && (predictionsForNounGroup.size() == 1 || predictionsForNounGroup.contains(VDOLabel.SERVICE_INTERRUPT))

                )
                    vectorCvss[7] = "H"; // availability H
                else if (predictionsForNounGroup.contains(VDOLabel.READ) || predictionsForNounGroup.contains(VDOLabel.INDIRECT_DISCLOSURE))
                    vectorCvss[5] = "LH"; // confidentiality LH
                else if (predictionsForNounGroup.contains(VDOLabel.WRITE) || predictionsForNounGroup.contains(VDOLabel.RESOURCE_REMOVAL))
                    vectorCvss[6] = "LH"; // integrity LH
                else if (predictionsForNounGroup.contains(VDOLabel.SERVICE_INTERRUPT))
                    vectorCvss[7] = "LH"; // availability LH

            } else if (vdoNounGroup == VDONounGroup.MITIGATION) {
                if (predictionsForNounGroup.contains(VDOLabel.SANDBOXED))
                    vectorCvss[4] = "C"; // we assume a scope change if "sandboxed" is feasible for mitigation

            }

        }

        return vectorCvss;
    }


    /**
     * New (august 2023) way of computing cvss scores. The previous methodology would check a cvss vector against a large dataset and return the median matching score.
     * This method loads a precomputed map from cvss vector -> score for a simple lookup instead.
     * @return map from cvss vector to median score among matching NVD vulnerabilities
     */
    private Map<CVSSVector, Double> loadScoreTable() {
        Map<CVSSVector, Double> out = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(ctx.getResource("classpath:cvss_map.csv").getFile()))) {
            String[] line;
            while ((line=reader.readNext()) != null) {
                out.put(new CVSSVector(line[0]), Double.parseDouble(line[1]));
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error while loading CVSS score map");
            logger.error(e);
        }
        return out;
    }

//    public static void main(String[] args) {
//        Set<VDOLabel> inputSet = new HashSet<>();
//        inputSet.add(VDOLabel.LIMITED_RMT);
//        inputSet.add(VDOLabel.LOCAL);
//        inputSet.add(VDOLabel.APPLICATION);
//        inputSet.add(VDOLabel.TRUST_FAILURE);
//        inputSet.add(VDOLabel.READ);
//        inputSet.add(VDOLabel.RESOURCE_REMOVAL);
//        inputSet.add(VDOLabel.SANDBOXED);
//        inputSet.add(VDOLabel.ASLR);
//        List<VdoCharacteristic> characteristics = new ArrayList<>();
//
//        VdoCharacteristic characteristic = new VdoCharacteristic(null, "Limited Rmt", "Attack Theater", 0);
//        characteristics.add(characteristic);
//
//        Set<VDOLabel> labels = mapToLabelSet(characteristics);
//        labels.forEach(v->System.out.println(v.vdoLabelName));
//        Map<CVSSVector, Double> scoreTable = loadScoreTable();
//        String[] vector = getCvssVector(inputSet);
//        Double score = scoreTable.get(new CVSSVector(vector));
//        System.out.println(score);
//    }

}
