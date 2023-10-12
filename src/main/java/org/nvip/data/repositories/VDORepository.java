package org.nvip.data.repositories;

import org.nvip.entities.VdoCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VDORepository extends JpaRepository<VdoCharacteristic, Long> {
}
