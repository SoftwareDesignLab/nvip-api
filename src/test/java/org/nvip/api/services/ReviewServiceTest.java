package org.nvip.api.services;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.data.repositories.*;
import org.nvip.entities.*;
import org.nvip.util.CvssGenUtil;
import org.nvip.util.Messenger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceTest {

    @Mock
    private VulnRepository vulnRepository;
    @Mock
    private VulnVersionRepository vulnVersionRepository;
    @Mock
    private RawDescRepository rawDescRepository;
    @Mock
    private DescriptionRepository descriptionRepository;
    @Mock
    private AffProdRepository affProdRepository;
    @Mock
    private CpeSetRepository cpeSetRepository;
    @Mock
    private VDORepository vdoRepository;
    @Mock
    private VdoSetRepository vdoSetRepository;
    @Mock
    private CvssGenUtil cvssGenUtil;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void complexUpdateDesc() {
        String cveId = "CVE-2023-1234";

        Vulnerability vuln = new Vulnerability();
        Vulnerability vulnSpy = spy(vuln);
        vulnSpy.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vulnSpy));
        VulnerabilityVersion vv = mock(VulnerabilityVersion.class);
        when(vv.getCpeSet()).thenReturn(mock(CpeSet.class));
        when(vv.getVulnerabilityVersionId()).thenReturn(0);
        doReturn(vv).when(vulnSpy).getCurrentVersion();
        doReturn(mock(List.class)).when(vulnSpy).getVdoCharacteristics();
        doReturn(List.of()).when(vulnSpy).getRawDescriptions();
        when(rawDescRepository.save(any())).thenReturn(mock(RawDescription.class));
        when(descriptionRepository.save(any())).thenReturn(mock(Description.class));
        when(vulnVersionRepository.save(any())).thenReturn(vv);

        reviewService.complexUpdate(1, "a", "CVE-2023-1234", "This is a sample description", null, null);
        verify(rawDescRepository, times(1)).save(any(RawDescription.class));
    }

    @Test
    void complexUpdateVDO() {
        String cveId = "CVE-2023-1234";

        Vulnerability vuln = new Vulnerability();
        Vulnerability vulnSpy = spy(vuln);
        vulnSpy.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vulnSpy));
        VulnerabilityVersion vv = mock(VulnerabilityVersion.class);
        when(vv.getCpeSet()).thenReturn(mock(CpeSet.class));
        when(vv.getVulnerabilityVersionId()).thenReturn(0);
        doReturn(vv).when(vulnSpy).getCurrentVersion();
        doReturn("Some description").when(vulnSpy).getDescriptionString();
        doReturn(mock(List.class)).when(vulnSpy).getVdoCharacteristics();
        doReturn(List.of()).when(vulnSpy).getRawDescriptions();
        when(rawDescRepository.save(any())).thenReturn(mock(RawDescription.class));
        when(descriptionRepository.save(any())).thenReturn(mock(Description.class));
        when(vulnVersionRepository.save(any())).thenReturn(vv);

        reviewService.complexUpdate(1, "a", "CVE-2023-1234", null, new VdoUpdate(new JSONObject("{vdoLabels:[{\n" +
                "  \"label\": \"Write\",\n" +
                "  \"group\": \"Logical Impact\",\n" +
                "  \"confidence\": 0,\n" +
                "  \"isActive\": 1\n" +
                "}]}")), null);
        verify(vdoSetRepository, times(1)).save(any());
    }

    @Test
    void complexUpdateAffProd() {
        String cveId = "CVE-2023-1234";

        Vulnerability vuln = new Vulnerability();
        Vulnerability vulnSpy = spy(vuln);
        vulnSpy.setCveId(cveId);
        when(vulnRepository.findByCveId(cveId)).thenReturn(java.util.Optional.of(vulnSpy));
        VulnerabilityVersion vv = mock(VulnerabilityVersion.class);
        when(vv.getCpeSet()).thenReturn(mock(CpeSet.class));
        when(vv.getVulnerabilityVersionId()).thenReturn(0);
        doReturn(vv).when(vulnSpy).getCurrentVersion();
        doReturn("Some description").when(vulnSpy).getDescriptionString();
        doReturn(mock(List.class)).when(vulnSpy).getVdoCharacteristics();
        doReturn(List.of()).when(vulnSpy).getRawDescriptions();
        when(rawDescRepository.save(any())).thenReturn(mock(RawDescription.class));
        when(descriptionRepository.save(any())).thenReturn(mock(Description.class));
        when(vulnVersionRepository.save(any())).thenReturn(vv);

        reviewService.complexUpdate( 1, "a", cveId, null, null, new int[]{1, 2, 3});
        verify(cpeSetRepository, times(1)).save(any());
    }

}