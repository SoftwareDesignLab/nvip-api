package org.nvip.data.repositories;

import org.nvip.entities.RawDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDescRepository extends JpaRepository<RawDescription, Long> {

}
