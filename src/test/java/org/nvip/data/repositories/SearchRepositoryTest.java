package org.nvip.data.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.nvip.entities.Vulnerability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
//@DataJpaTest
public class SearchRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    SearchRepository repository;

    @Test
    @Transactional
    void testGetSearchResultsReturnsNothingWhenNoVulnerabilitiesFound(){
        Map<Integer, List<Vulnerability>> vulnerabilities = repository.getSearchResultsByID("CVE-1234-5678");
        assertTrue(vulnerabilities.isEmpty());
    }

    @Test
    @Transactional
    void testGetSearchResultsReturnsVulnWhenOneVulnerabilitiesFound(){
        Vulnerability vuln = new Vulnerability(1, "CVE-1234-5678", "Description", "Platform", Instant.now().toString(), Instant.now().toString(), true, true);
        entityManager.persist(vuln);

        Map<Integer, List<Vulnerability>> vulnerabilities = repository.getSearchResultsByID("CVE-1234-5678");
        assertFalse(vulnerabilities.isEmpty());
        assertEquals(1, vulnerabilities.size());

        entityManager.remove(vuln);
    }
}
