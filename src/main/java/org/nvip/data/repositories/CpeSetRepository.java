package org.nvip.data.repositories;

import org.nvip.entities.CpeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CpeSetRepository extends JpaRepository<CpeSet, Long> {
}
