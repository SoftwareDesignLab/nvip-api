package org.nvip.data.repositories;

import org.nvip.entities.Vulnerability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VulnRepository extends JpaRepository<Vulnerability, Long> {
    //TODO: this should soon have all vuln related queries - merge with Vulnerability Repository functions
    Vulnerability findByCveId(String cveId);

    @Query("SELECT v FROM Vulnerability v WHERE v.createdDate >= :startDate AND v.createdDate < :endDate")
    List<Vulnerability> findByCreatedDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
