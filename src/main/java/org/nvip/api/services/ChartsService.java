package org.nvip.api.services;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.ChartsDTO;
import org.nvip.data.repositories.MainRepository;
import org.nvip.entities.RunHistory;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartsService {

    private final MainRepository mainRepository;

    ChartsDTO toDTO(Map<String, String> map) {
        return ChartsDTO.builder()
                .not_in_mitre_count(map.get("not_in_mitre_count"))
                .not_in_nvd_count(map.get("not_in_nvd_count"))
                .cvesUpdated(map.get("CvesUpdated"))
                .cvesAdded(map.get("CvesAdded"))
                .avgTimeGapNvd(map.get("avgTimeGapNvd"))
                .avgTimeGapMitre(map.get("avgTimeGapMitre"))
                .run_date_times(map.get("run_date_times"))
                .build();
    }

    // convert query results to key value pairs expected by UI
    // with run data separated by semicolons
    Map<String, String> groupConcat(List<RunHistory> runs) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // return key value pairs with semicolons separating runs
        return Map.of(
                "not_in_mitre_count", runs.stream().map(RunHistory::getNotInMitreCount).map(String::valueOf).collect(Collectors.joining(";")),
                "not_in_nvd_count", runs.stream().map(RunHistory::getNotInNvdCount).map(String::valueOf).collect(Collectors.joining(";")),
                "CvesUpdated", runs.stream().map(RunHistory::getUpdatedCveCount).map(String::valueOf).collect(Collectors.joining(";")),
                "CvesAdded", runs.stream().map(RunHistory::getNewCveCount).map(String::valueOf).collect(Collectors.joining(";")),
                "avgTimeGapNvd", runs.stream().map(RunHistory::getAvgTimeGapNvd).map(String::valueOf).collect(Collectors.joining(";")),
                "avgTimeGapMitre", runs.stream().map(RunHistory::getAvgTimeGapMitre).map(String::valueOf).collect(Collectors.joining(";")),
                "run_date_times", runs.stream().map(RunHistory::getRunDateTime).map(dt -> dt.format(formatter)).collect(Collectors.joining(";"))
        );
    }

     public ChartsDTO getMainPageCounts() {
         return toDTO(groupConcat(mainRepository.findTop15ByOrderByRunDateTimeDesc()));
     }

}
