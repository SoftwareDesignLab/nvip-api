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

import org.nvip.api.serializers.CvssUpdate;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.entities.*;
import org.nvip.util.Messenger;

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

	private Vulnerability getVulnerability(String cve_id) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Vulnerability> cq = criteriaBuilder.createQuery(Vulnerability.class);
		Root<Vulnerability> root = cq.from(Vulnerability.class);
		CriteriaQuery<Vulnerability> query = cq.where(criteriaBuilder.equal(root.get("cveId"), cve_id));
		return entityManager.createQuery(query).getSingleResult();
	}

	/**
	 * Updates description of a vulnerability in vulnerabilities table
	 * @param description
	 * @param vuln_id
	 * @return
	 */
	@Transactional
	public void updateVulnerabilityDescription(String description, String cve_id, String username) {
		Vulnerability vuln = getVulnerability(cve_id);

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
		entityManager.persist(rawDesc);

		//SEND MESSAGE TO RABBITMQ
		Messenger.sendCveId(cve_id);
	}

	/**
	 * Updates the CVSS of a given Vulnerability
	 * @param cvssUpdate
	 * @param cve_id
	 * @return
	 */
	@Transactional
	public void updateVulnerabilityCVSS(CvssUpdate cvssUpdate, String cve_id, int user_id) {
		for (CvssUpdateRecord cvssRecord : cvssUpdate.getCvssRecords()){
	        Cvss cvss = new Cvss(getVulnerability(cve_id), cvssRecord.getBaseScore(), cvssRecord.getImpactScore(), cvssRecord.getCreatedDate(), user_id);
	        this.entityManager.persist(cvss);
	    }
	}

	/**
	 * Deletes entries from VdoCharacteristic table by CVE-ID
	 * and replaces them with new entries from vdoUpdate parameter
	 * @param vdoUpdate - Contains an Arraylist of VDOupdateInfo objects that obtains
	 * the new information to be inserted into VdoCharacteristic
	 * @param cve_id - ID of CVE that needs to have VDO updated
	 * @return
	 */
	@Transactional
	public void updateVulnerabilityVDO(VdoUpdate vdoUpdate, String cve_id, int user_id) {
        for (VdoUpdateRecord vdoRecord : vdoUpdate.getVdoRecords()){
        	VdoCharacteristic vdo = new VdoCharacteristic(getVulnerability(cve_id), vdoRecord.getCreatedDate(), vdoRecord.getLabel(), vdoRecord.getGroup(), vdoRecord.getConfidence(), user_id);
        	this.entityManager.persist(vdo);
        }
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
	public int removeProductsFromVulnerability(int[] productIds, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

		int result = 0;
 
 		for (int prodId : productIds) {
	        // create delete
	        CriteriaDelete<AffectedProduct> delete = cb.createCriteriaDelete(AffectedProduct.class);
	 
	        // set the root class
	        Root root = delete.from(AffectedProduct.class);
	 
	        // set delete and where clause
	        delete.where(cb.and(root.get("affectedProductId").in(prodId), root.get("vulnerability").get("cveId").in(cve_id)));
	 
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
	public int complexUpdate(boolean updateDescription, boolean updateVDO, boolean updateCVSS, boolean updateAffRel, int vuln_id, int user_id, String username, String cve_id,
			String cveDescription, VdoUpdate vdoUpdate, CvssUpdate cvssUpdate, int[] productsToRemove) {

		int rs = 0;

		if (updateDescription) {
			updateVulnerabilityDescription(cveDescription, cve_id, username);
		}

		if (updateVDO) {
			updateVulnerabilityVDO(vdoUpdate, cve_id, user_id);
		}

		if (updateCVSS) {
			updateVulnerabilityCVSS(cvssUpdate, cve_id, user_id);
		}

		if (updateAffRel) {
			rs += removeProductsFromVulnerability(productsToRemove, cve_id);
		}

		return rs;

	}

}