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
import org.nvip.data.DBConnect;
import org.nvip.entities.CvssScore;
import org.nvip.entities.Product;
import org.nvip.entities.VdoCharacteristic;
import org.nvip.entities.Vulnerability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.nvip.util.VulnerabilityUtil;

import java.sql.*;
import java.time.LocalDate;
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
	private static String dbType = DBConnect.getDatabaseType();
	private final static int defaultLimit = 10000;

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
	private static HashMap<String, String[]> parseSearchInfo(String infoType, String infoArrStr) {
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
	public static Map<String, Map<String, String[]>> getSearchInfo() {
		try (Connection conn = DBConnect.getConnection()) {
			Map<String, Map<String, String[]>> searchMap = new HashMap<>();

			CallableStatement stmt = conn.prepareCall("CALL getSearchFormInfo()");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				searchMap = Stream
						.of(new String[][] { { "cvssScores", rs.getString("cvss_scores") },
								{ "vdoNounGroups", rs.getString("vdo_noun_groups") } })
						.collect(Collectors.toMap(data -> data[0], data -> parseSearchInfo(data[0], data[1])));
			}

			return searchMap;
		} catch (SQLException e) {
			logger.error(e.toString());
		}

		return null;
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

		Query query = entityManager.createNativeQuery(
		"""
			SELECT
				v.vuln_id,
				v.cve_id,
				v.description,
				v.platform,
				v.published_date,
				v.exists_at_mitre,
				v.exists_at_nvd,
				v.last_modified_date,
				ar.version,
				p.product_id,
				p.cpe,
				p.domain,
				group_concat(vc.vdo_confidence SEPARATOR ';') AS vdo_label_confidences,
				group_concat(vc.vdo_label_id SEPARATOR ';') AS label_ids,
				group_concat(vl.vdo_label_name SEPARATOR ';') AS vdo_labels,
				group_concat(vn.vdo_noun_group_name SEPARATOR ';') AS vdo_noun_groups,
				cvsever.cvss_severity_class as base_severity,
				cvscore.severity_confidence,
				cvscore.impact_score,
				cvscore.impact_confidence,
				ex.publisher_url
			FROM vulnerability v
				LEFT JOIN exploit ex ON ex.vuln_id = v.vuln_id
				LEFT JOIN affectedrelease ar ON ar.cve_id = v.cve_id
				LEFT JOIN vdocharacteristic vc ON vc.cve_id = v.cve_id
				LEFT JOIN cvssscore cvscore ON cvscore.cve_id = v.cve_id
				LEFT JOIN product p ON p.product_id = ar.product_id
				LEFT JOIN vdolabel vl ON vl.vdo_label_id = vc.vdo_label_id
				LEFT JOIN vdonoungroup vn ON vn.vdo_noun_group_id = vl.vdo_noun_group_id
				LEFT JOIN cvssseverity cvsever ON cvsever.cvss_severity_id = cvscore.cvss_severity_id
			WHERE v.cve_id = ?
			GROUP BY
				v.vuln_id,
				v.cve_id,
				v.description,
				v.platform,
				v.published_date,
				v.exists_at_mitre,
				v.exists_at_nvd,
				v.last_modified_date,
				ar.version,
				p.product_id,
				p.cpe,
				p.domain,
				cvsever.cvss_severity_class,
				cvscore.severity_confidence,
				cvscore.impact_score,
				cvscore.impact_confidence,
				ex.publisher_url;""", Tuple.class
		).setParameter(1, cve_id);

		try {
			Tuple result = (Tuple) query.getSingleResult();
			Vulnerability vulnerability = new Vulnerability(
					result.get("vuln_id", Integer.class),
					result.get("cve_id", String.class),
					result.get("description", String.class),
					result.get("platform", String.class),
					result.get("published_date", String.class),
					result.get("last_modified_date", String.class),
					result.get("exists_at_mitre", Boolean.class),
					result.get("exists_at_nvd", Boolean.class)
			);

			VdoCharacteristic[] vdoList = VulnerabilityUtil.parseVDOList(
					result.get("cve_id", String.class),
					result.get("vdo_labels", String.class),
					result.get("vdo_label_confidences", String.class),
					result.get("vdo_noun_groups", String.class)
			);
			vulnerability.setVdoList(vdoList);

			CvssScore[] cvssScoreList = vulnerabilityUtil.parseCvssScoreList(
					result.get("cve_id", String.class),
					result.get("base_severity", String.class),
					result.get("severity_confidence", String.class),
					result.get("impact_score", String.class),
					result.get("impact_confidence", String.class)
			);
			vulnerability.setCvssScoreList(cvssScoreList);

			Product[] products = VulnerabilityUtil.parseProductList(
					result.get("product_id", String.class),
					result.get("cpe", String.class),
					result.get("domain", String.class),
					result.get("version", String.class)
			);
			vulnerability.setProducts(products);

			searchResults.add(vulnerability);

			searchResultMap.put(1, searchResults);
		} catch (Exception e){
			logger.debug("CVE not Found");
			logger.warn(e);
		}

		return searchResultMap;
	}

	/**
	 * Calls a stored procedure to obtain vulnerabilities that match the parameters
	 * passed in. Returns the set of vulnerabilities ordered by earliest update.
	 * through the search form.
	 * 
	 * @param vulnId        - Vulnerability id. Used to define range of results
	 *                      (above or below given id)
	 * @param keyword       - Keyword that is within the vulnerability description
	 * @param startDate     - Starting date for last update of the vulnerabilities
	 * @param endDate       - End date for last update of the vulnerabilities
	 * @param cvssScores    - Set of CVSS scores values which the vulnerabilities
	 *                      must contain (at least one)
	 * @param vdoNounGroups - Set of VDO Noun Groups which the vulnerabilities must
	 *                      contain (at least one)
	 * @param vdoLabels     - Set of VDO labels which the vulnerabilities must
	 *                      contain (at least one)
	 * @param inMitre       - Parameter which indicates vulnerability has an entry
	 *                      on MITRE
	 * @param inNvd         - Parameter which indicates vulnerability has an entry
	 *                      on NVD
	 * @param limitCount    - Sets the limit of vulnerabilities returned. Defaults
	 *                      to the set default if not provided
	 * @param isBefore      - Defines if search range before or after given id
	 * @return Map of a list of vulnerabilities to the total count of those
	 *         vulnerabilities
	 */
	public Map<Integer, List<Vulnerability>> getSearchResults(int vulnId, String keyword, LocalDate startDate,
			LocalDate endDate, String[] cvssScores, String[] vdoLabels, int limitCount, String product) {



		return null;
	}

	public static void main(String[] args) {

	}
}