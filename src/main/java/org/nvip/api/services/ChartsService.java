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
