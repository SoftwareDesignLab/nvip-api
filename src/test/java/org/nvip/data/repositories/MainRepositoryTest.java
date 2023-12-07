/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.nvip.data.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.api.services.ReviewService;
import org.nvip.entities.VdoUpdateRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    VulnRepository vulnRepository;

    @Autowired
    VulnVersionRepository vulnVersionRepository;

    @Autowired
    VDORepository vdoRepository;

    @Autowired
    AffProdRepository affProdRepository;

    @Autowired
    RawDescRepository rawDescRepository;

    @Autowired
    DescriptionRepository descriptionRepository;

    @Autowired
    RawDescriptionJTRepository rawDescriptionJTRepository;

    @Autowired
    CpeSetRepository cpeSetRepository;

    @Autowired
    VdoSetRepository vdoSetRepository;

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
