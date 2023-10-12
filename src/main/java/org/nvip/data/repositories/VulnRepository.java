package org.nvip.data.repositories;

import org.nvip.entities.Vulnerability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VulnRepository extends JpaRepository<Vulnerability, Long> {
    //TODO: this should soon have all vuln related queries - merge with Vulnerability Repository functions
    Vulnerability findByCveId(String cveId);
}
