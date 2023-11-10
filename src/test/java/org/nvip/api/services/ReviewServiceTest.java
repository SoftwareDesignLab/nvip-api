package org.nvip.api.services;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.data.repositories.AffProdRepository;
import org.nvip.data.repositories.RawDescRepository;
import org.nvip.data.repositories.VDORepository;
import org.nvip.data.repositories.VulnRepository;
import org.nvip.entities.Description;
import org.nvip.entities.RawDescription;
import org.nvip.entities.Vulnerability;
import org.nvip.util.Messenger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private VulnRepository vulnRepository;
    @Mock
    private RawDescRepository rawDescRepository;
    @Mock
    private VDORepository vdoRepository;
    @Mock
    private AffProdRepository affProdRepository;

    @Test
    void updateVulnerabilityDescription() {
        // arrange sample description
        String description = "This is a sample description";
        Description desc = new Description();
        desc.setDescription(description);
        String cveId = "CVE-2023-1234";
        String username = "a";
        Vulnerability vuln = new Vulnerability();
        vuln.setCveId(cveId);
        vuln.setDescription(desc);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vuln));
        // act update vulnerability description
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        reviewService.updateVulnerabilityDescription(description, cveId, username);
        // assert
        verify(rawDescRepository, times(1)).save(any(RawDescription.class));
    }

    @Test
    void updateVulnerabilityVDO() {
        // arrange
        VdoUpdate vdoUpdate = new VdoUpdate(new JSONObject("{vdoLabels:[{\n" +
                "  \"label\": \"Write\",\n" +
                "  \"group\": \"Logical Impact\",\n" +
                "  \"confidence\": 0,\n" +
                "  \"isActive\": 1\n" +
                "}]}"));
        int userId = 1;
        String cveId = "CVE-2023-1234";
        Vulnerability vuln = new Vulnerability();
        vuln.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vuln));
        // act
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        reviewService.updateVulnerabilityVDO(vdoUpdate, cveId, userId);
        // assert
        verify(vdoRepository, times(1)).save(any());
    }

    @Test
    void removeProductsFromVulnerability() {
        int[] productIds = {1, 2, 3};
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        reviewService.removeProductsFromVulnerability(productIds);
        verify(affProdRepository, times(3)).deleteByAffectedProductId(anyInt());
    }

    @Test
    void complexUpdateDesc() {
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        String cveId = "CVE-2023-1234";
        Vulnerability vuln = new Vulnerability();
        vuln.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vuln));
        reviewService.complexUpdate(true, false, false, 1, "a", "CVE-2023-1234", "This is a sample description", null, null);
        verify(rawDescRepository, times(1)).save(any(RawDescription.class));
    }

    @Test
    void complexUpdateVDO() {
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        String cveId = "CVE-2023-1234";
        Vulnerability vuln = new Vulnerability();
        vuln.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vuln));
        reviewService.complexUpdate(false, true, false, 1, "a", "CVE-2023-1234", null, new VdoUpdate(new JSONObject("{vdoLabels:[{\n" +
                "  \"label\": \"Write\",\n" +
                "  \"group\": \"Logical Impact\",\n" +
                "  \"confidence\": 0,\n" +
                "  \"isActive\": 1\n" +
                "}]}")), null);
        verify(vdoRepository, times(1)).save(any());
    }

    @Test
    void complexUpdateAffProd() {
        ReviewService reviewService = new ReviewService(rawDescRepository, vulnRepository, vdoRepository, affProdRepository);
        reviewService.complexUpdate(false, false, true, 1, "a", "CVE-2023-1234", null, null, new int[]{1, 2, 3});
        verify(affProdRepository, times(3)).deleteByAffectedProductId(anyInt());
    }

}