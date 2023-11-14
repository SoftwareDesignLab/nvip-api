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
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MainRepository {

	@PersistenceContext
	EntityManager entityManager;

	private static final String[] MAIN_PAGE_COUNTS = { "CvesAdded", "CvesUpdated", "not_in_nvd_count",
			"not_in_mitre_count", "run_date_times", "avgTimeGapNvd", "avgTimeGapMitre" };

	public Map<String, String> getMainPageCounts() {

		Query query = entityManager.createNativeQuery("""
			SELECT
				group_concat(drh.not_in_mitre_count SEPARATOR ';') not_in_mitre,
				group_concat(drh.not_in_nvd_count SEPARATOR ';') not_in_nvd,
				group_concat(drh.run_date_time SEPARATOR ';') run_date_time,
				group_concat(drh.new_cve_count SEPARATOR ';') added_cve_count,
				group_concat(drh.updated_cve_count SEPARATOR ';') updated_cve_count
			FROM (
				SELECT run_date_time, not_in_nvd_count, not_in_mitre_count, new_cve_count, updated_cve_count
				FROM runhistory ORDER BY run_date_time DESC LIMIT 15
			) AS drh;
			""", Tuple.class);

		Map<String, String> mainPageCounts = new HashMap<>();
		Tuple res = ((Tuple) query.getSingleResult());
		if(res.get("added_cve_count", String.class) != null) mainPageCounts.put(MAIN_PAGE_COUNTS[0], res.get("added_cve_count", String.class));
		if(res.get("updated_cve_count", String.class) != null) mainPageCounts.put(MAIN_PAGE_COUNTS[1], res.get("updated_cve_count", String.class));
		if(res.get("not_in_nvd", String.class) != null) mainPageCounts.put(MAIN_PAGE_COUNTS[2], res.get("not_in_nvd", String.class));
		if(res.get("not_in_mitre", String.class) != null) mainPageCounts.put(MAIN_PAGE_COUNTS[3], res.get("not_in_mitre", String.class));
		if(res.get("run_date_time", String.class) != null) mainPageCounts.put(MAIN_PAGE_COUNTS[4], res.get("run_date_time", String.class));

		return mainPageCounts;
	}
}