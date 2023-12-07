/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.nvip.data.repositories;

import org.nvip.entities.Vulnerability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface VulnRepository extends JpaRepository<Vulnerability, Long> {

    Optional<Vulnerability> findByCveId(String cveId);

    @Query("SELECT v FROM Vulnerability v WHERE v.createdDate >= :startDate AND v.createdDate < :endDate")
    List<Vulnerability> findByCreatedDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //TODO: can probably simplify based on which search params are given
    @Query("SELECT DISTINCT v " +
            "FROM Vulnerability v " +
            "LEFT JOIN VulnerabilityVersion vv " +
            "LEFT JOIN v.exploits e " +
            "LEFT JOIN vv.cpeSet.affectedProducts ap " +
            "LEFT JOIN vv.vdoSet.vdoCharacteristics vc " +
            "WHERE (:keyword IS NULL OR vv.description.description LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR v.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR v.createdDate <= :endDate) " +
            "AND (vv.vdoSet.cvssBaseScore IN :cvssScores) " +
            "AND (:product IS NULL OR ap.cpe LIKE %:product%) " +
            "AND (vc.vdoLabel IN :vdoLabels) " +
            "GROUP BY v.vulnId " +
            "ORDER BY v.vulnId DESC " +
            "LIMIT :limitCount")
    List<Vulnerability> searchVulnerabilities(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("cvssScores") double[] cvssScores,
            @Param("vdoLabels") String[] vdoLabels,
            @Param("limitCount") Integer limitCount,
            @Param("product") String product
    );
}
