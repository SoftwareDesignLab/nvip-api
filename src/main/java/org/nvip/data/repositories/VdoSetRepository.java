package org.nvip.data.repositories;

import org.nvip.entities.VdoSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VdoSetRepository extends JpaRepository<VdoSet, Long> {
}
