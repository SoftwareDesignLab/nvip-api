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

		searchResults = Stream
				.of(new String[][] { { "cvssScores", "CRITICAL;HIGH;LOW;MEDIUM" },
						{ "vdoNounGroups", "Impact Method:Man-in-the-Middle;Authentication Bypass;Code Execution;Context Escape;Trust Failure|Context:Channel;Physical Hardware;Application;Host OS;Firmware;Guest OS;Hypervisor|Mitigation:Physical Security;MultiFactor Authentication;HPKP/HSTS;ASLR;Sandboxed|Attack Theater:Limited Rmt;Local;Remote;Physical|Logical Impact:Read;Resource Removal;Write;Indirect Disclosure;Service Interrupt;Privilege Escalation" } })
				.collect(Collectors.toMap(data -> data[0], data -> parseSearchInfo(data[0], data[1])));

		return searchResults;
	}
}