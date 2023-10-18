package org.nvip.data.repositories;

import org.nvip.entities.Vulnerability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VulnRepository extends JpaRepository<Vulnerability, Long> {

    Vulnerability findByCveId(String cveId);

    @Query("SELECT v FROM Vulnerability v WHERE v.createdDate >= :startDate AND v.createdDate < :endDate")
    List<Vulnerability> findByCreatedDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //TODO: can probably simplify based on which search params are given
    @Query("SELECT DISTINCT v " +
            "FROM Vulnerability v " +
            "LEFT JOIN v.exploits e " +
            "LEFT JOIN v.affectedProducts ap " +
            "LEFT JOIN v.vdoCharacteristics vc " +
            "LEFT JOIN v.cvssScore c " +
            "WHERE (:keyword IS NULL OR v.description.description LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR v.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR v.createdDate <= :endDate) " +
            "AND (c.baseScore IN :cvssScores) " +
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
//    @Query("SELECT DISTINCT v " +
//            "FROM Vulnerability v " +
//            "LEFT JOIN v.exploits e " +
//            "LEFT JOIN v.affectedProducts ap " +
//            "LEFT JOIN v.vdoCharacteristics vc " +
//            "LEFT JOIN v.cvssScore c " +
//            "WHERE (:keyword IS NULL OR v.description.description LIKE %:keyword%) " +
//            "AND (:startDate IS NULL OR v.createdDate >= :startDate) " +
//            "AND (:endDate IS NULL OR v.createdDate <= :endDate) " +
//            "AND ((:cvssScores) IS NULL OR c.baseScore IN (:cvssScores)) " +
//            "AND (:product IS NULL OR ap.cpe LIKE %:product%) " +
//            "AND ((:vdoLabels) IS NULL OR vc.vdoLabel IN (:vdoLabels)) " +
//            "GROUP BY v.vulnId " +
//            "ORDER BY v.vulnId DESC " +
//            "LIMIT :limitCount")
//    List<Vulnerability> searchVulnerabilities(
//            @Param("keyword") String keyword,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate,
//            @Param("cvssScores") double[] cvssScores,
//            @Param("vdoLabels") String[] vdoLabels,
//            @Param("limitCount") Integer limitCount,
//            @Param("product") String product
//    );
}
