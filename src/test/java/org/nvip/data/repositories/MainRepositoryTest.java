package org.nvip.data.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.junit.Ignore;
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

//    @Test
//    @Transactional
//    void testPageCountsReturnEmptyMapWhenNoRunHistory(){
//        Map<String, String> counts = repository.getMainPageCounts();
//        assertTrue(counts.isEmpty());
//    }

    @Test
    @Ignore
    @Transactional
    void testPageCountsSingleHistoryEntry(){
        Query query = entityManager.createNativeQuery("""
            INSERT INTO runhistory
            (runhistory_id, run_date_time, not_in_nvd_count, not_in_mitre_count, not_in_both_count, avg_time_gap_nvd, avg_time_gap_mitre, total_cve_count, new_cve_count, updated_cve_count)
            VALUES
            (
                1, CURDATE(), 1, 2, 3, 4, 5, 6, 7, 8
            );
            """);
        query.executeUpdate();

        // TODO: Fix/remove
//        Map<String, String> counts = repository.getMainPageCounts();
//
//        assertNotNull(counts.get("run_date_times"));
//        assertEquals("1", counts.get("not_in_nvd_count"));
//        assertEquals("2", counts.get("not_in_mitre_count"));
//        assertEquals("4.0", counts.get("avgTimeGapNvd"));
//        assertEquals("5.0", counts.get("avgTimeGapMitre"));
//        assertEquals("7", counts.get("CvesAdded"));
//        assertEquals("8", counts.get("CvesUpdated"));
    }

    @Test
    @Ignore
    @Transactional
    void testPageCountsMultipleHistoryEntry(){
        Query query = entityManager.createNativeQuery("""
            INSERT INTO runhistory
            (runhistory_id, run_date_time, not_in_nvd_count, not_in_mitre_count, not_in_both_count, avg_time_gap_nvd, avg_time_gap_mitre, total_cve_count, new_cve_count, updated_cve_count)
            VALUES
            (1, CURDATE(), 1, 2, 3, 4, 5, 6, 7, 8),
            (2, CURDATE()-1, 9, 10, 11, 12, 13, 14, 15, 16)
            ;
            """);
        query.executeUpdate();

        // TODO: Fix/remove
//        Map<String, String> counts = repository.getMainPageCounts();
//                System.out.println(counts);
//
//
//        assertNotNull(counts.get("run_date_times"));
//        assertEquals("1;9", counts.get("not_in_nvd_count"));
//        assertEquals("2;10", counts.get("not_in_mitre_count"));
//        assertEquals("4.0;12.0", counts.get("avgTimeGapNvd"));
//        assertEquals("5.0;13.0", counts.get("avgTimeGapMitre"));
//        assertEquals("7;15", counts.get("CvesAdded"));
//        assertEquals("8;16", counts.get("CvesUpdated"));
    }
}
