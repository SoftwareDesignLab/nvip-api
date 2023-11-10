package org.nvip.api.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nvip.api.serializers.ChartsDTO;
import org.nvip.data.repositories.MainRepository;
import org.nvip.entities.RunHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChartsServiceTest {

    @Mock
    private MainRepository mainRepository;

    @Test
    void testChartDataToDTO() {
        ChartsService chartsService = new ChartsService(mainRepository);
        Map<String, String> testChartData = Map.of(
                "not_in_mitre_count", "1",
                "not_in_nvd_count", "2",
                "CvesUpdated", "3",
                "CvesAdded", "4",
                "avgTimeGapNvd", "5",
                "avgTimeGapMitre", "6",
                "run_date_times", "7"
        );
        ChartsDTO chartsDTO = chartsService.toDTO(testChartData);
        assertEquals("1", chartsDTO.getNot_in_mitre_count());
        assertEquals("2", chartsDTO.getNot_in_nvd_count());
        assertEquals("3", chartsDTO.getCvesUpdated());
        assertEquals("4", chartsDTO.getCvesAdded());
        assertEquals("5", chartsDTO.getAvgTimeGapNvd());
        assertEquals("6", chartsDTO.getAvgTimeGapMitre());
        assertEquals("7", chartsDTO.getRun_date_times());
    }

    @Test
    void testGroupConcatToKeyValues() {
        ChartsService chartsService = new ChartsService(mainRepository);
        RunHistory runHistory = RunHistory.builder()
                .notInMitreCount(1)
                .notInNvdCount(2)
                .updatedCveCount(3)
                .newCveCount(4)
                .avgTimeGapNvd(5.0)
                .avgTimeGapMitre(6.0)
                .runDateTime(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                .build();
        RunHistory runHistory2 = RunHistory.builder()
                .notInMitreCount(1)
                .notInNvdCount(2)
                .updatedCveCount(3)
                .newCveCount(4)
                .avgTimeGapNvd(5.0)
                .avgTimeGapMitre(6.0)
                .runDateTime(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                .build();
        Map<String, String> testChartData = chartsService.groupConcat(List.of(runHistory, runHistory2));
        assertEquals("1;1", testChartData.get("not_in_mitre_count"));
        assertEquals("2;2", testChartData.get("not_in_nvd_count"));
        assertEquals("3;3", testChartData.get("CvesUpdated"));
        assertEquals("4;4", testChartData.get("CvesAdded"));
        assertEquals("5.0;5.0", testChartData.get("avgTimeGapNvd"));
        assertEquals("6.0;6.0", testChartData.get("avgTimeGapMitre"));
        assertEquals("2021-01-01 01:01:01;2021-01-01 01:01:01", testChartData.get("run_date_times"));
    }

    @Test
    void testMainPageCounts() {
        ChartsService chartsService = new ChartsService(mainRepository);
        List<RunHistory> runs = List.of(
                RunHistory.builder()
                        .notInMitreCount(1)
                        .notInNvdCount(2)
                        .updatedCveCount(3)
                        .newCveCount(4)
                        .avgTimeGapNvd(5.0)
                        .avgTimeGapMitre(6.0)
                        .runDateTime(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                        .build()
        );
        Mockito.when(mainRepository.findTop15ByOrderByRunDateTimeDesc())
                .thenReturn(runs);
        ChartsDTO chartsDTO = chartsService.getMainPageCounts();
        assertEquals("1", chartsDTO.getNot_in_mitre_count());
        assertEquals("2", chartsDTO.getNot_in_nvd_count());
        assertEquals("3", chartsDTO.getCvesUpdated());
        assertEquals("4", chartsDTO.getCvesAdded());
        assertEquals("5.0", chartsDTO.getAvgTimeGapNvd());
        assertEquals("6.0", chartsDTO.getAvgTimeGapMitre());
        assertEquals("2021-01-01 01:01:01", chartsDTO.getRun_date_times());
    }
}