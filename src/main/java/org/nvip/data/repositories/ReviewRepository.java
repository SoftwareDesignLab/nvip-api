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
		Join<Vulnerability, VulnerabilityUpdate> updateJoin = root.join("updates", JoinType.LEFT);
		Join<VulnerabilityUpdate, DailyRunHistory> dailyRunHistoryJoin = updateJoin.join("dailyRunHistory", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(root.get("cveId").in(cveID));

		cq = cq.where(predicates.toArray(new Predicate[0]));
		Query q = entityManager.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Updates Daily Vulnerability
	 * @param dateRange
	 * @return
	 */
	@Deprecated
	public int updateDailyVulnerability(int dateRange) {
		try (Connection conn = DBConnect.getConnection();
				CallableStatement stmt = conn.prepareCall("CALL prepareDailyVulnerabilities(?, ?, ?)")) {

			LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).plusDays(1);

			stmt.setTimestamp(1, Timestamp.valueOf(today.minusDays(dateRange)));
			stmt.setTimestamp(2, Timestamp.valueOf(today));

			stmt.registerOutParameter("cveCount", Types.INTEGER);

			stmt.execute();

			return stmt.getInt("cveCount");

		} catch (SQLException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		return -1;
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

    	UserVulnerabilityUpdate uvu = new UserVulnerabilityUpdate(user_id, cve_id, today, info);
    	this.entityManager.persist(uvu);

    	return result;
	}

	/**
	 * Helper function for atomicUpdateVulnerability,
	 * provides database connection and runs query to update uservulnerabilityupdate
	 * table
	 * @param conn
	 * @param status_id
	 * @param vuln_id
	 * @param user_id
	 * @param cve_id
	 * @param info
	 * @return
	 */
	// public int atomicUpdateVulnerability(Connection conn, int status_id, int vuln_id, int user_id, String cve_id, String info) {

	// 	try (PreparedStatement stmt1 = conn.prepareStatement("UPDATE vulnerability SET last_modified_date= ?, status_id = ? WHERE vuln_id = ?;");
	// 		 PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO nvip.uservulnerabilityupdate (user_id, cve_id, datetime, info) VALUES (?, ?, ?, ?)")) {

	// 		LocalDateTime today = LocalDateTime.now();

	// 		stmt1.setTimestamp(1, Timestamp.valueOf(today));
	// 		stmt1.setInt(2, status_id);
	// 		stmt1.setInt(3, vuln_id);

	// 		int rs = stmt1.executeUpdate();

	// 		stmt2.setInt(1, user_id);
	// 		stmt2.setString(2, cve_id);
	// 		stmt2.setTimestamp(3, Timestamp.valueOf(today));
	// 		stmt2.setString(4, info);

	// 		rs = stmt2.executeUpdate();

	// 		return rs;

	// 	} catch (SQLException e) {
	// 		logger.error(e.toString());
	// 		e.printStackTrace();
	// 	}

	// 	return -1;

	// }

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

		// try(PreparedStatement stmt = conn.prepareStatement("UPDATE vulnerability SET description = ? WHERE vuln_id=?")) {

		// 	stmt.setString(1, description);
		// 	stmt.setInt(2, vuln_id);
		// 	int rs = stmt.executeUpdate();

		// 	return rs;

		// } catch (SQLException e) {
		// 	logger.error(e.toString());
		// 	e.printStackTrace();
		// }

		// return -1;
	}

	/**
	 * Updates the CVSS of a given Vulnerability
	 * @param cvssUpdate
	 * @param cve_id
	 * @return
	 */
	@Transactional
	public int updateVulnerabilityCVSS(CVSSupdate cvssUpdate, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create delete
        CriteriaDelete<CvssScore> delete = cb.createCriteriaDelete(CvssScore.class);
 
        // set the root class
        Root root = delete.from(CvssScore.class);
 
        // set delete and where clause
        delete.where(root.get("vulnerability").get("cveId").in(cve_id));
 
        // perform delete
        int result = this.entityManager.createQuery(delete).executeUpdate();

        CvssScore cvss = new CvssScore(getVulnerabilityDetails(cve_id).get(0), cvssUpdate.getSeverity_confidence(), ""+cvssUpdate.getImpact_score(), cvssUpdate.getImpact_confidence());
        String severityName = "";
        switch (cvssUpdate.getCvss_severity_id()) {
        	case 1:
        		severityName = "HIGH";
        		break;
        	case 2:
        		severityName = "MEDIUM";
        		break;
        	case 3:
        		severityName = "n/a";
        		break;
        	case 4:
        		severityName = "CIRITICAL";
        		break;
        	case 5:
        		severityName = "LOW";
        		break;
        }
        cvss.setCvssSeverity(new CvssSeverity(cvssUpdate.getCvss_severity_id(), severityName));

        this.entityManager.persist(cvss);


        return result;


		// try(PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM cvssscore WHERE cve_id = ?");
		// 		PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO cvssscore (cve_id, cvss_severity_id, severity_confidence, impact_score, impact_confidence) VALUES (?,?,?,?,?)")) {
			
		// 	stmt1.setString(1, cve_id);
		// 	int rs = stmt1.executeUpdate();

		// 	stmt2.setString(1, cve_id);
		// 	stmt2.setInt(2, cvssUpdate.getCvss_severity_id());
		// 	stmt2.setDouble(3, cvssUpdate.getSeverity_confidence());
		// 	stmt2.setDouble(4, cvssUpdate.getImpact_score());
		// 	stmt2.setDouble(5, cvssUpdate.getImpact_confidence());
		// 	rs = stmt2.executeUpdate();

		// 	return rs;

		// } catch (SQLException e) {
		// 	logger.error(e.toString());
		// 	e.printStackTrace();
		// }

		// return -1;

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
	public int updateVulnerabilityVDO(VDOupdateInfo vdoUpdate, String cve_id) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
 
        // create delete
        CriteriaDelete<VdoCharacteristic> delete = cb.createCriteriaDelete(VdoCharacteristic.class);
 
        // set the root class
        Root root = delete.from(VdoCharacteristic.class);
 
        // set delete and where clause
        delete.where(root.get("vulnerability").get("cveId").in(cve_id));
 
        // perform delete
        int result = this.entityManager.createQuery(delete).executeUpdate();


        for (VDOupdateRecord vdoRecord : vdoUpdate.getVdoRecords()){
        	VdoCharacteristic vdo = new VdoCharacteristic(cve_id, ""+vdoRecord.getLabelID(), vdoRecord.getConfidence(), ""+vdoRecord.getGroupID());
        	this.entityManager.persist(vdo);
        }
        


        return result;
		// try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM vdocharacteristic WHERE cve_id = ?");
		// 		PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO vdocharacteristic (cve_id, vdo_label_id,vdo_confidence,vdo_noun_group_id) VALUES (?,?,?,?)")){

		// 	stmt1.setString(1, cve_id);
		// 	int rs = stmt1.executeUpdate();

		// 	for (int i = 0; i < vdoUpdate.getVdoRecords().size(); i++) {
		// 		stmt2.setString(1, cve_id);
		// 		stmt2.setInt(2, vdoUpdate.getVdoRecords().get(i).getLabelID());
		// 		stmt2.setDouble(3, vdoUpdate.getVdoRecords().get(i).getConfidence());
		// 		stmt2.setInt(4, vdoUpdate.getVdoRecords().get(i).getGroupID());
		// 		rs = stmt2.executeUpdate();
		// 	}

		// 	return rs;

		// } catch (SQLException e) {
		// 	logger.error(e.toString());
		// 	e.printStackTrace();
		// }

		// return -1;

	}

	/**
	 * Deletes provided affectedRelease entries
	 * Will loop through all productsIDs and delete entries with the same
	 * productID and CVE-ID
	 * 
	 * @param conn - Connection to database
	 * @param productsID - Array of product IDs to be deleted from AffectedRelease Table
	 * @param cve_id - cve_id to be deleted from AffectedRelease table
	 * @return 
	 * 
	 */
	public int removeProductsFromVulnerability(Connection conn, int[] productsID, String cve_id) {
		try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM AffectedRelease where product_id = ?  AND cve_id = ?") ){

			int rs = 0;

			for (int i = 0; i < productsID.length; i++) {
				stmt.setInt(1, productsID[i]);
				stmt.setString(2, cve_id);
				rs = stmt.executeUpdate();
			}

			return rs;

		} catch (SQLException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		return -1;

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
			String cveDescription, VDOupdateInfo vdoUpdate, CVSSupdate cvssUpdate, int[] productsToRemove) {

		try(Connection conn = DBConnect.getConnection()) {
			conn.setAutoCommit(false);

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
				rs = removeProductsFromVulnerability(conn, productsToRemove, cve_id);
			}

			rs = atomicUpdateVulnerability(status_id, vuln_id, user_id, cve_id, updateInfo);

			conn.commit();

		} catch (SQLException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		return -1;

	}

}