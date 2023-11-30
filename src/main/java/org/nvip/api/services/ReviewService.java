package org.nvip.api.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.data.repositories.AffProdRepository;
import org.nvip.data.repositories.RawDescRepository;
import org.nvip.data.repositories.VDORepository;
import org.nvip.data.repositories.VulnRepository;
import org.nvip.entities.*;
import org.nvip.util.AppException;
import org.nvip.util.Messenger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RawDescRepository rawDescRepository;
    private final VulnRepository vulnRepository;
    private final VDORepository vdoRepository;
    private final AffProdRepository affProdRepository;

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
    public void complexUpdate(boolean updateDescription, boolean updateVDO, boolean updateAffRel, int user_id, String username, String cve_id,
                              String cveDescription, VdoUpdate vdoUpdate, int[] productsToRemove) {

        if (updateDescription) {
            updateVulnerabilityDescription(cveDescription, cve_id, username);
        }

        if (updateVDO) {
            updateVulnerabilityVDO(vdoUpdate, cve_id, user_id);
        }

        if (updateAffRel) {
            removeProductsFromVulnerability(productsToRemove);
        }

    }
}
