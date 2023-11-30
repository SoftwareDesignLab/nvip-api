package org.nvip.data.repositories;

import org.nvip.entities.VdoCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VDORepository extends JpaRepository<VdoCharacteristic, Long> {
}
