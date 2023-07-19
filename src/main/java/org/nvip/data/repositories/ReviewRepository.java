/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.nvip.data.repositories;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.nvip.api.serializers.CVSSupdate;
import org.nvip.api.serializers.VDOupdateInfo;
import org.nvip.data.DBConnect;
import org.nvip.entities.*;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Repository
public class ReviewRepository {

	@PersistenceContext
	EntityManager entityManager;

	private static Logger logger = LogManager.getLogger(ReviewRepository.class);

	/**
	 * TODO: Refactor this to use GROUP CONCAT for VDO Labels and products
	 * Obtains details of a specific vulnerability
	 * @param cveID
	 * @return
	 */
	public List<Vulnerability> getVulnerabilityDetails(String cveID) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Vulnerability> cq = criteriaBuilder.createQuery(Vulnerability.class);
		Root<Vulnerability> root = cq.from(Vulnerability.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(root.get("cveId").in(cveID));

		cq = cq.where(predicates.toArray(new Predicate[0]));
		Query q = entityManager.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Adds manual update log to uservulnerabilityupdate table to keep track
	 * of manual updates to vulnerabilities
	 * @param status_id
	 * @param vuln_id
	 * @param user_id
	 * @param cve_id
	 * @param info
	 * @return
	 */
	@Transactional
	public int atomicUpdateVulnerability(int status_id, int vuln_id, int user_id, String cve_id, String info) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create update
        CriteriaUpdate<Vulnerability> update = cb.createCriteriaUpdate(Vulnerability.class);
 
        // set the root class
        Root root = update.from(Vulnerability.class);

        LocalDateTime today = LocalDateTime.now();
 
        // set update and where clause
        update.set("lastModifiedDate", Timestamp.valueOf(today));
        update.set("statusId", status_id);
        update.where(root.get("vulnId").in(vuln_id));
 
        // perform update
        int result = this.entityManager.createQuery(update).executeUpdate();

        // Currently no UserVulnerabilityUpdate in the new DB schema
    	// UserVulnerabilityUpdate uvu = new UserVulnerabilityUpdate(user_id, cve_id, today, info);
    	// this.entityManager.persist(uvu);

    	return result;
	}

	/**
	 * Updates description of a vulnerability in vulnerabilities table
	 * @param description
	 * @param vuln_id
	 * @return
	 */
	@Transactional
	public int updateVulnerabilityDescription(String description, int vuln_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create update
        CriteriaUpdate<Vulnerability> update = cb.createCriteriaUpdate(Vulnerability.class);
 
        // set the root class
        Root root = update.from(Vulnerability.class);

        LocalDateTime today = LocalDateTime.now();
 
        // set update and where clause
        update.set("description", description);
        update.where(root.get("vulnId").in(vuln_id));
 
        // perform update
        return this.entityManager.createQuery(update).executeUpdate();
	}

	/**
	 * Updates the CVSS of a given Vulnerability
	 * @param cvssUpdate
	 * @param cve_id
	 * @return
	 */
	@Transactional
	public int updateVulnerabilityCVSS(CvssUpdate cvssUpdate, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create delete
        CriteriaDelete<Cvss> delete = cb.createCriteriaDelete(Cvss.class);
 
        // set the root class
        Root root = delete.from(Cvss.class);
 
        // set delete and where clause
        delete.where(root.get("vulnerability").get("cveId").in(cve_id));
 
        // perform delete
        int result = this.entityManager.createQuery(delete).executeUpdate();

        Cvss cvss = new Cvss(getVulnerabilityDetails(cve_id).get(0), cvssUpdate.getBaseScore(), cvssUpdate.getImpactScore(), cvssUpdate.getCreatedDate());

        this.entityManager.persist(cvss);

        return result;
	}

	private static final String[] labels = {
		"Man-in-the-Middle",
		"Channel",
		"Authentication Bypass",
		"Physical Hardware",
		"Application",
		"Host OS",
		"Firmware",
		"Code Execution",
		"Context Escape",
		"Guest OS",
		"Hypervisor",
		"Sandboxed",
		"Physical Security",
		"ASLR",
		"Limited Rmt",
		"Local",
		"Read",
		"Resource Removal",
		"HPKP/HSTS",
		"MultiFactor Authentication",
		"Remote",
		"Write",
		"Indirect Disclosure",
		"Service Interrupt",
		"Privilege Escalation",
		"Physical"
	};

	/**
	 * Deletes entries from VdoCharacteristic table by CVE-ID
	 * and replaces them with new entries from vdoUpdate parameter
	 * @param vdoUpdate - Contains an Arraylist of VDOupdateInfo objects that obtains
	 * the new information to be inserted into VdoCharacteristic
	 * @param cve_id - ID of CVE that needs to have VDO updated
	 * @return
	 */
	@Transactional
	public int updateVulnerabilityVDO(CvssUpdate vdoUpdate, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create delete
        CriteriaDelete<VdoCharacteristic> delete = cb.createCriteriaDelete(VdoCharacteristic.class);
 
        // set the root class
        Root root = delete.from(VdoCharacteristic.class);
 
        // set delete and where clause
        delete.where(root.get("vulnerability").get("cveId").in(cve_id));
 
        // perform delete
        int result = this.entityManager.createQuery(delete).executeUpdate();



        // for (VDOupdateRecord vdoRecord : vdoUpdate.getVdoRecords()){
        // 	String noun = "";
        // 	switch(vdoRecord.getGroupID()) {
        // 	case 1:
        // 		noun="ImpactMethod";
        // 		break;
        // 	case 2:
        // 		noun="Context";
        // 		break;
        // 	case 3:
        // 		noun="Mitigation";
        // 		break;
        // 	case 4:
        // 		noun="AttackTheater";
        // 		break;
        // 	case 5:
        // 		noun="LogicalImpact";
        // 		break;
        // 	}
        // 	VDOgroup group = new VDOgroup(vdoRecord.getGroupID(), noun);
        // 	VdoLabel label = new VdoLabel(vdoRecord.getLabelID(), labels[vdoRecord.getLabelID() - 1], group);
        // 	VdoCharacteristic vdo = new VdoCharacteristic(getVulnerabilityDetails(cve_id).get(0), "", vdoRecord.getConfidence(), "");
        // 	vdo.setVdoGroup(group);
        // 	vdo.setVdoLabels(label);
        // 	logger.info(vdo);
        // 	this.entityManager.persist(vdo);
        // }
        
        return result;
	}

	/**
	 * Deletes provided affectedRelease entries
	 * Will loop through all productsIDs and delete entries with the same
	 * productID and CVE-ID
	 * 
	 * @param productsID - Array of product IDs to be deleted from AffectedRelease Table
	 * @param cve_id - cve_id to be deleted from AffectedRelease table
	 * @return 
	 * 
	 */
	@Transactional
	public int removeProductsFromVulnerability(int[] productsID, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

		int result = 0;
 
 		for (int prodId : productsID) {
	        // create delete
	        CriteriaDelete<AffectedRelease> delete = cb.createCriteriaDelete(AffectedRelease.class);
	 
	        // set the root class
	        Root root = delete.from(AffectedRelease.class);
	 
	        // set delete and where clause
	        delete.where(cb.and(root.get("product").get("productId").in(prodId), root.get("vulnerability").get("cveId").in(cve_id)));
	 
	        // perform delete
	        result += this.entityManager.createQuery(delete).executeUpdate();
	    }

	    return result;
	}

	/**
	 * Runs a full update on Vulnerabilities tables as per request from
	 * ReviewServlet
	 * @param updateDescription - Checks if description needs to be updated
	 * @param updateVDO - Checks if VDO needs to be updated
	 * @param updateCVSS - Checks if CVSS needs to be updated
	 * @param updateAffRel - Checks if Affected Releases table needs to be updated
	 * @param status_id
	 * @param vuln_id
	 * @param user_id
	 * @param cve_id
	 * @param updateInfo - Info on update (For atomic update logs)
	 * @param cveDescription - New CVE Description
	 * @param vdoUpdate - New VDO Info
	 * @param cvssUpdate - New CVSS Info
	 * @param productsToRemove - Products to remove from Affected Releases
	 * @return -1
	 */
	@Transactional
	public int complexUpdate(boolean updateDescription, boolean updateVDO, boolean updateCVSS, boolean updateAffRel, int status_id, int vuln_id, int user_id, String cve_id, String updateInfo,
			String cveDescription, VDOupdateInfo vdoUpdate, CvssUpdate cvssUpdate, int[] productsToRemove) {

		int rs = 0;

		if (updateDescription) {
			rs = updateVulnerabilityDescription(cveDescription, vuln_id);
		}

		if (updateVDO) {
			rs = updateVulnerabilityVDO(vdoUpdate, cve_id);
		}

		if (updateCVSS) {
			rs = updateVulnerabilityCVSS(cvssUpdate, cve_id);
		}

		if (updateAffRel) {
			rs = removeProductsFromVulnerability(productsToRemove, cve_id);
		}

		rs = atomicUpdateVulnerability(status_id, vuln_id, user_id, cve_id, updateInfo);

		return -1;

	}

}