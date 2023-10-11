package org.nvip.api.controllers;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.ChartsDTO;
import org.nvip.api.services.ChartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    final ChartsService chartsService;

    @GetMapping("/")
    public ResponseEntity<ChartsDTO> getVulnerabilitySummaries(){
        return ResponseEntity.ok(chartsService.getMainPageCounts());
    }
}
