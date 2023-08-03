package org.nvip.api.controllers;

import org.nvip.api.serializers.VulnerabilityForReviewDTO;
import org.nvip.api.serializers.CvssUpdate;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.data.repositories.ReviewRepository;
import org.nvip.data.repositories.UserRepository;
import org.nvip.data.repositories.VulnerabilityRepository;
import org.nvip.entities.*;
import org.nvip.util.TwitterApi;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Collections;
import java.io.IOException;
import java.io.BufferedReader;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private static final Logger logger = LogManager.getLogger(ReviewController.class);

    final UserRepository userRepository;
    final ReviewRepository reviewRepository;
    final VulnerabilityRepository vulnerabilityRepository;

    public ReviewController(UserRepository userRepository, ReviewRepository reviewRepository, VulnerabilityRepository vulnerabilityRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    @PostMapping
    public ResponseEntity<String> createReview(
        @RequestParam(value="username") String userName,
        @RequestParam(value="token") String token,
        @RequestParam(value="vulnId") int vulnID,
        @RequestParam(value="cveId") String cveID,

        @RequestParam(value="updateDescription", required=false, defaultValue="false") boolean updateDescription,
        @RequestParam(value="updateVDO", required=false, defaultValue="false") boolean updateVDO,
        @RequestParam(value="updateCVSS", required=false, defaultValue="false") boolean updateCVSS,
        @RequestParam(value="updateAffRel", required=false, defaultValue="false") boolean updateAffRel,

        @RequestBody String updateData
    ) 
    {
        logger.info("Body test: {}", updateData);
        if (userName == null || token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        User user = userRepository.getRoleIDandExpirationDate(userName, token);

        if (user == null || user.getRoleId() < 1 || user.getRoleId() > 2)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

       int userID = user.getUserID();

       JSONObject dataJSON = new JSONObject(updateData);

       String cveDescription = null;
       VdoUpdate vdoUpdate = null;
       CvssUpdate cvssUpdate = null;
       int[] productsToRemove = null;

       if (updateDescription) {
           cveDescription = dataJSON.getString("description");
       }

       if (updateVDO) {
           vdoUpdate = new VdoUpdate(dataJSON.getJSONObject("vdoUpdates"));
       }

       if (updateCVSS) {
           cvssUpdate = new CvssUpdate(dataJSON.getJSONArray("cvss"));
       }

       if (updateAffRel) {
           JSONArray jsonArray = dataJSON.getJSONArray("prodToRemove");
           productsToRemove = new int[jsonArray.length()];
           for (int i = 0; i < jsonArray.length(); i++) {
               productsToRemove[i] = jsonArray.getInt(i);
           }
       }

       reviewRepository.complexUpdate(updateDescription, updateVDO, updateCVSS, updateAffRel, vulnID, userID, userName, cveID, cveDescription, vdoUpdate, cvssUpdate,
               productsToRemove);

       return ResponseEntity.status(HttpStatus.OK).body("");
   }
}
