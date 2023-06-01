package org.nvip.api.controllers;

import org.nvip.data.repositories.MainRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class MainController {

    final MainRepository mainRepository;

    public MainController(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    @GetMapping
    public Map<String, String> getVulnerabilitySummaries(){
        return mainRepository.getMainPageCounts();
    }
}
