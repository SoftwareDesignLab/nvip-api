package org.nvip.data.repositories;

import org.nvip.entities.VulnerabilityVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VulnVersionRepository extends JpaRepository<VulnerabilityVersion, Long> {

}
