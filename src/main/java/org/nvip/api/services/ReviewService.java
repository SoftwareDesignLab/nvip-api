package org.nvip.api.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.data.repositories.*;
import org.nvip.entities.*;
import org.nvip.util.AppException;
import org.nvip.util.CvssGenUtil;
import org.nvip.util.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @NotNull
    private final CvssGenUtil cvssGenUtil;

    private final VulnRepository vulnRepository;
    private final VulnVersionRepository vulnVersionRepository;
    private final VDORepository vdoRepository;
    private final AffProdRepository affProdRepository;
    private final RawDescRepository rawDescRepository;
    private final DescriptionRepository descriptionRepository;
    private final RawDescriptionJTRepository rawDescriptionJTRepository;
    private final CpeSetRepository cpeSetRepository;
    private final VdoSetRepository vdoSetRepository;

//    private Vulnerability getVulnerability(String cve_id) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Vulnerability> cq = criteriaBuilder.createQuery(Vulnerability.class);
//        Root<Vulnerability> root = cq.from(Vulnerability.class);
//        CriteriaQuery<Vulnerability> query = cq.where(criteriaBuilder.equal(root.get("cveId"), cve_id));
//        return entityManager.createQuery(query).getSingleResult();
//    }

    @Transactional
    public void updateVulnerabilityDescription(String description, String cve_id, String username) {
        Vulnerability vuln = vulnRepository.findByCveId(cve_id)
                .orElseThrow(() -> new AppException("Vulnerability not found with id: " + cve_id, HttpStatus.NOT_FOUND));
        RawDescription rawDesc = new RawDescription(
                description,
                vuln,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "usersource-"+username,
                0,
                "usersource-"+username,
                "usersource-"+username
        );
        rawDescRepository.save(rawDesc);
        //SEND MESSAGE TO RABBITMQ
        Messenger.sendCveId(cve_id);
    }

    // TODO: Remove, deprecated
    @Transactional
    public void updateVulnerabilityVDO(VdoUpdate vdoUpdate, String cve_id, int user_id) {
        // persist new changes
        for (VdoUpdateRecord vdoRecord : vdoUpdate.getVdoRecords()){
            VdoCharacteristic vdo = new VdoCharacteristic(
                    vulnRepository.findByCveId(cve_id)
                            .orElseThrow(() -> new AppException("Cannot update VDO, unable to find vuln with id" + cve_id, HttpStatus.NOT_FOUND)),
                    vdoRecord.getCreatedDate(),
                    vdoRecord.getLabel(),
                    vdoRecord.getGroup(),
                    vdoRecord.getConfidence(),
                    user_id,
                    vdoRecord.getIsActive());
            vdoRepository.save(vdo);
        }
    }

    @Transactional
    public void removeProductsFromVulnerability(int[] productIds) {
        for (int prodId : productIds) {
            affProdRepository.deleteByAffectedProductId(prodId);
        }
    }

    @Transactional
    public void complexUpdate(int user_id, String username, String cve_id,
                              String cveDescription, VdoUpdate vdoUpdate, int[] productsToRemove) {

        // Get vuln by cveId and get current vuln version
        final Vulnerability vuln = vulnRepository.findByCveId(cve_id).orElseThrow();
        VulnerabilityVersion currentVersion = vuln.getCurrentVersion();

        // Update VdoSet
        final VdoSet newVdoSet = updateVdoSet(user_id, vdoUpdate, vuln);

        // Update CpeSet
        final CpeSet newCpeSet = updateCpeSet(user_id, productsToRemove, currentVersion.getCpeSet(), vuln);

        // Update Description
        String inputDesc = cveDescription == null ? vuln.getDescriptionString() : cveDescription;
        Object[] rawThenDesc = updateDescription(username, inputDesc, vuln);
        final Description description = (Description) rawThenDesc[1];
        final RawDescription rawDesc = (RawDescription) rawThenDesc[0];

//        final RawDescriptionJT rawDescJt = jtInsert(rawDesc, description);

        // Build new VulnerabilityVersion
        VulnerabilityVersion vv = new VulnerabilityVersion(
                vuln,
                newVdoSet,
                newCpeSet,
                description,
                LocalDateTime.now(),
                currentVersion.getPublishedDate(),
                LocalDateTime.now(),
                user_id
        );
        vv = vulnVersionRepository.save(vv);

        vuln.setVulnVersionId(vv.getVulnerabilityVersionId());

        vulnRepository.save(vuln);
    }

    private VdoSet updateVdoSet(int user_id, VdoUpdate vdoUpdate, Vulnerability vuln) {
        // Update vdoSet
        // persist new changes
        List<VdoCharacteristic> vdoCharacteristics;
        double cvssScore;
        if (vdoUpdate == null) {
            vdoCharacteristics = vuln.getVdoCharacteristics()
                    .stream()
                    .map(v -> new VdoCharacteristic(
                            vuln,
                            v.getCreatedDate(),
                            v.getVdoLabel(),
                            v.getVdoNounGroup(),
                            1.,
                            user_id,
                            v.getIsActive()))
                    .collect(Collectors.toList());
            cvssScore = cvssGenUtil.calculateCVSSScore(vuln.getVdoCharacteristics());
        } else {
            vdoCharacteristics = vdoUpdate.getVdoRecords()
                    .stream()
                    .filter(v->v.getIsActive()==1)
                    .map(vdoRecord -> new VdoCharacteristic(
                            vuln,
                            vdoRecord.getCreatedDate(),
                            vdoRecord.getLabel(),
                            vdoRecord.getGroup(),
                            vdoRecord.getConfidence(),
                            user_id,
                            vdoRecord.getIsActive())
                    ).collect(Collectors.toList());

            cvssScore = cvssGenUtil.calculateCVSSScoreFromUpdates(vdoUpdate.getVdoRecords());
        }
        VdoSet newVdoSet = new VdoSet(
                LocalDateTime.now(),
                vdoCharacteristics,
                cvssScore,
                user_id,
                vuln.getCveId()
        );
        System.out.println(newVdoSet);

        return vdoSetRepository.save(newVdoSet);
    }

    @Transactional
    public Object[] updateDescription(String username, String cveDescription, Vulnerability vuln) {
        // Update description
        RawDescription rawDesc = new RawDescription(
                cveDescription,
                vuln,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "usersource-" + username,
                0,
                "user",
                "usersource-" + username
        );
        rawDesc = rawDescRepository.save(rawDesc);

        Description description = new Description(
                rawDesc.getRawDescription(),
                LocalDateTime.now(),
                null, // "(" + existing_func + "," + rawDesc.getId() + ")"
                1,
                vuln.getCveId()
        );
        description.getRawDescriptions().add(rawDesc);
        description.getRawDescriptions().addAll(vuln.getRawDescriptions());
        description = descriptionRepository.save(description);
//        descriptionRepository.flush();
//        rawDescriptionJTRepository.flush();
        return new Object[] {rawDesc, description};
    }

//    private RawDescriptionJT jtInsert(RawDescription rawDesc, Description description) {
//        // persist the rawdesc
//        RawDescriptionJT rawDescriptionJT = new RawDescriptionJT(
//                rawDesc,
//                description
//        );
//        RawDescriptionJT rawJT = rawDescriptionJTRepository.save(rawDescriptionJT);
//        rawDescriptionJTRepository.flush();
//        return rawJT;
//    }

    private CpeSet updateCpeSet(int user_id, int[] productsToRemove, CpeSet cpeSet, Vulnerability vuln) {
        // Update cpeSet
        final List<AffectedProduct> existingAffectedProducts = cpeSet.getAffectedProducts();

        final List<AffectedProduct> updatedAffectedProducts = new ArrayList<>();

        final CpeSet newCpeSet = new CpeSet(
                LocalDateTime.now(),
                updatedAffectedProducts,
                user_id,
                vuln.getCveId()
        );

        updatedAffectedProducts.addAll(existingAffectedProducts
                .stream()
                .filter(ap -> !ArrayUtils.contains(productsToRemove, ap.getAffectedProductId()))
                .map(ap -> new AffectedProduct(
                        ap.getVulnerability(),
                        newCpeSet,
                        ap.getCpe(),
                        ap.getProductName(),
                        ap.getVersion(),
                        ap.getVendor(),
                        ap.getPurl(),
                        ap.getSwidTag()
                ))
                .toList());

        return cpeSetRepository.save(newCpeSet);
    }
}
