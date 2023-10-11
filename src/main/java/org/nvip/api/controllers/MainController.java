package org.nvip.api.controllers;

import lombok.RequiredArgsConstructor;
import org.nvip.data.repositories.MainRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainController {

    final MainRepository mainRepository;

    @GetMapping("/")
    public Map<String, String> getVulnerabilitySummaries(){
        return mainRepository.getMainPageCounts();
    }
}
