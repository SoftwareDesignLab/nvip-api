package org.nvip.data.repositories;

import org.nvip.entities.Description;
import org.nvip.entities.RawDescriptionJT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptionRepository extends JpaRepository<Description, Long> {
}
