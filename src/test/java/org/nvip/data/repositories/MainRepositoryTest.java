package org.nvip.data.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
//@DataJpaTest
public class MainRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MainRepository repository;

    @Test
    @Transactional
    void testPageCountsReturnEmptyMapWhenNoRunHistory(){
        Map<String, String> counts = repository.getMainPageCounts();
        assertTrue(counts.isEmpty());
    }

    @Test
    @Transactional
    void testPageCountsSingleHistoryEntry(){
        Query query = entityManager.createNativeQuery("""
            INSERT INTO dailyrunhistory
            (run_id, run_date_time, not_in_nvd_count, not_in_mitre_count, avg_time_gap_nvd, avg_time_gap_mitre, added_cve_count, updated_cve_count)
            VALUES
            (
                1, CURDATE(), 1, 2, 3, 4, 5, 6
            );
            """);
        query.executeUpdate();

        Map<String, String> counts = repository.getMainPageCounts();

        assertNotNull(counts.get("run_date_times"));
        assertEquals("1", counts.get("not_in_nvd_count"));
        assertEquals("2", counts.get("not_in_mitre_count"));
        assertEquals("3.0", counts.get("avgTimeGapNvd"));
        assertEquals("4.0", counts.get("avgTimeGapMitre"));
        assertEquals("5", counts.get("CvesAdded"));
        assertEquals("6", counts.get("CvesUpdated"));
    }

    @Test
    @Transactional
    void testPageCountsMultipleHistoryEntry(){
        Query query = entityManager.createNativeQuery("""
            INSERT INTO dailyrunhistory
            (run_id, run_date_time, not_in_nvd_count, not_in_mitre_count, avg_time_gap_nvd, avg_time_gap_mitre, added_cve_count, updated_cve_count)
            VALUES
            (1, CURDATE(), 1, 2, 3, 4, 5, 6),
            (2, CURDATE()-1, 7, 8, 9, 10, 11, 12)
            ;
            """);
        query.executeUpdate();

        Map<String, String> counts = repository.getMainPageCounts();

        assertNotNull(counts.get("run_date_times"));
        assertEquals("1;7", counts.get("not_in_nvd_count"));
        assertEquals("2;8", counts.get("not_in_mitre_count"));
        assertEquals("3.0;9.0", counts.get("avgTimeGapNvd"));
        assertEquals("4.0;10.0", counts.get("avgTimeGapMitre"));
        assertEquals("5;11", counts.get("CvesAdded"));
        assertEquals("6;12", counts.get("CvesUpdated"));
    }
}
