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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nvip.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.nvip.util.VulnerabilityUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SearchRepository {

	@Autowired VulnerabilityUtil vulnerabilityUtil;

	@PersistenceContext
	EntityManager entityManager;

	private static final Logger logger = LogManager.getLogger(SearchRepository.class);

	/**
	 * Parses the information retrieved from the Search Form initialization query
	 * and splits the returned strings into arrays.
	 * 
	 * @param infoType   Type of form info that is being returned (i.e. CVSS Scores,
	 *                   VDO labels)
	 * @param infoArrStr Delimited string containing all the values needed to
	 *                   initialize the Search Form for the given info type
	 * @return Map with the info type as the key and an array containing all the
	 *         entities for the given info type
	 */
	private HashMap<String, String[]> parseSearchInfo(String infoType, String infoArrStr) {
		HashMap<String, String[]> infoMap = new HashMap<>();

		if (infoType == "cvssScores") {
			infoMap.put(infoType, infoArrStr.split(";"));
		} else if (infoType == "vdoNounGroups") {
			String[] vdoEntities = infoArrStr.split("\\|");

			for (String vdoEntity : vdoEntities) {
				String[] vdo = vdoEntity.split(":");
				infoMap.put(vdo[0], vdo[1].split(";"));
			}
		}

		return infoMap;
	}

	/**
	 * Calls a stored procedure containing labels for parameters that can be
	 * searched in the search form (i.e. VDO Noun Groups, VDO Labels, CVSS Score
	 * labels, etc.)
	 * 
	 * @return Map containing parameter name of labels (i.e. CVSS Scores) and the
	 *         label strings
	 */
	public Map<String, Map<String, String[]>> getSearchInfo() {
		Map<String, Map<String, String[]>> searchResults = null;

		// Query query = entityManager.createNativeQuery("CALL getSearchFormInfo()", Tuple.class);
		// List<Tuple> results = (ArrayList<Tuple>) query.getResultList();
		// logger.info("Results: {}", results.size());

		// for(Tuple row: results) {
			// logger.info("Row: {}\nRow: {}", row.get("cvss_scores", String.class), row.get("vdo_noun_groups", String.class));

			searchResults = Stream
					.of(new String[][] { { "cvssScores", "CRITICAL;HIGH;LOW;MEDIUM" },
							{ "vdoNounGroups", "Impact Method:Man-in-the-Middle;Authentication Bypass;Code Execution;Context Escape;Trust Failure|Context:Channel;Physical Hardware;Application;Host OS;Firmware;Guest OS;Hypervisor|Mitigation:Physical Security;MultiFactor Authentication;HPKP/HSTS;ASLR;Sandboxed|Attack Theater:Limited Rmt;Local;Remote;Physical|Logical Impact:Read;Resource Removal;Write;Indirect Disclosure;Service Interrupt;Privilege Escalation" } })
					.collect(Collectors.toMap(data -> data[0], data -> parseSearchInfo(data[0], data[1])));
		// }

		logger.info(searchResults);

		return searchResults;
	}

	/**
	 * Conducts a query to search for a specific CVE by it's ID within the database
	 * 
	 * @param cve_id
	 * @return
	 * @throws SQLException
	 */
	public Map<Integer, List<Vulnerability>> getSearchResultsByID(String cve_id) {
		List<Vulnerability> searchResults = new ArrayList<>();
		HashMap<Integer, List<Vulnerability>> searchResultMap = new HashMap<Integer, List<Vulnerability>>();

	// 	Query query = entityManager.createNativeQuery(
	// 	"""
	// 		SELECT
	// 			v.vuln_id,
	// 			v.cve_id,
	// 			v.platform,
	// 			v.published_date,
	// 			""" +
	// 			// v.exists_at_mitre,
	// 			// v.exists_at_nvd,
	// 			"""
	// 			v.last_modified_date,
	// 			v.created_date,
	// 			ds.description_id,
	// 			ds.description,
	// 			ds.created_date AS desc_created_date,
	// 			ds.gpt_func
	// 			ap.version,
	// 			ap.product_name,
	// 			ap.cpe,
	// 			ap.domain,
	// 			group_concat(vc.vdo_confidence SEPARATOR ';') AS vdo_label_confidences,
	// 			group_concat(vc.vdo_label SEPARATOR ';') AS vdo_labels,
	// 			group_concat(vc.vdo_noun_group SEPARATOR ';') AS vdo_noun_groups,
	// 			cvscore.base_score,
	// 			cvscore.impact_score,
	// 			ex.publisher_url
	// 		FROM vulnerability v
	// 			LEFT JOIN description ds ON ds.cve_id = v.cve_id
	// 			LEFT JOIN exploit ex ON ex.vuln_id = v.vuln_id
	// 			LEFT JOIN affectedproduct ap ON af.cve_id = v.cve_id
	// 			LEFT JOIN vdocharacteristic vc ON vc.cve_id = v.cve_id
	// 			LEFT JOIN cvss cvscore ON cvscore.cve_id = v.cve_id
	// 		WHERE v.cve_id = ?
	// 		GROUP BY
	// 			v.vuln_id,
	// 			v.cve_id,
	// 			v.description,
	// 			v.platform,
	// 			v.published_date,
	// 			""" + 
	// 			// v.exists_at_mitre,
	// 			// v.exists_at_nvd,
	// 			"""
	// 			v.last_modified_date,
	// 			ap.version,
	// 			ap.product_name,
	// 			ap.cpe,
	// 			ap.domain,
	// 			cvscore.base_score,
	// 			cvscore.impact_score,
	// 			ex.publisher_url;""", Tuple.class
	// 	).setParameter(1, cve_id);

	// 	try {
	// 		Tuple result = (Tuple) query.getSingleResult();
	// 		Vulnerability vulnerability = new Vulnerability(
	// 			result.get("vuln_id", Integer.class),
	// 			result.get("cve_id", String.class),
	// 			LocalDateTime.parse(result.get("published_date", String.class)),
	// 			LocalDateTime.parse(result.get("last_modified_date", String.class)),
	// 			LocalDateTime.parse(result.get("created_date", String.class))
	// 		);

	// 		Description desc = new Description(
	// 			result.get("description_id", Integer.class),
	// 			result.get("description", String.class),
	// 			LocalDateTime.parse(result.get("desc_created_date", String.class)),
	// 			result.get("gpt_func", String.class)
	// 		);
	// 		desc.setVulnerability(vulnerability);

	// 		VdoCharacteristic[] vdoList = VulnerabilityUtil.parseVDOList(
	// 			result.get("cve_id", String.class),
	// 			result.get("vdo_labels", String.class),
	// 			result.get("vdo_label_confidences", String.class),
	// 			result.get("vdo_noun_groups", String.class)
	// 		);
	// 		vulnerability.setVdoList(vdoList);

	// 		CvssScore[] cvssScoreList = vulnerabilityUtil.parseCvssScoreList(
	// 			result.get("cve_id", String.class),
	// 			result.get("base_score", String.class),
	// 			result.get("impact_score", String.class)
	// 		);
	// 		vulnerability.setCvssScoreList(cvssScoreList);

	// 		AffectedProduct[] products = VulnerabilityUtil.parseProductList(
	// 			result.get("product_name", String.class),
	// 			result.get("cpe", String.class),
	// 			result.get("domain", String.class),
	// 			result.get("version", String.class)
	// 		);
	// 		vulnerability.setProducts(products);

	// 		searchResults.add(vulnerability);

	// 		searchResultMap.put(1, searchResults);
	// 	} catch (Exception e){
	// 		logger.debug("CVE not Found");
	// 		logger.warn(e);
	// 	}

		return searchResultMap;
	}
}