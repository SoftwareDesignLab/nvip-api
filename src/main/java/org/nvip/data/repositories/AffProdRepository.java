package org.nvip.data.repositories;

import org.nvip.entities.AffectedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffProdRepository extends JpaRepository<AffectedProduct, Long> {
    void deleteByAffectedProductId(int id);
}
