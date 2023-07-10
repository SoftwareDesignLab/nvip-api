package org.nvip.api.controllers;

import org.nvip.api.serializers.VulnerabilityForReviewDTO;
import org.nvip.api.serializers.CVSSupdate;
import org.nvip.api.serializers.VDOupdateInfo;
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

    @GetMapping
    public ResponseEntity<List<VulnerabilityForReviewDTO>> searchForReviews(
            @RequestParam(value="username") String userName,
            @RequestParam(value="token") String token,
            @RequestParam(value="cveId", required = false) String cveID,
            @RequestParam(value="searchDate", required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate searchDate,
            @RequestParam(value="crawled", required = false, defaultValue = "false") Boolean crawled,
            @RequestParam(value="rejected", required = false, defaultValue = "false") Boolean rejected,
            @RequestParam(value="accepted", required = false, defaultValue = "false") Boolean accepted,
            @RequestParam(value="reviewed", required = false, defaultValue = "false") Boolean reviewed
    )
    {

        if (userName == null || token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        User user = userRepository.getRoleIDandExpirationDate(userName, token);

        if (user == null || user.getRoleId() < 1 || user.getRoleId() > 2)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);


        if (cveID != null && cveID != "") {
           List<Vulnerability> vulns = reviewRepository.getVulnerabilityDetails(cveID);
           return ResponseEntity.status(HttpStatus.OK).body(vulns.stream().map(v -> {
                VulnerabilityForReviewDTO.VulnerabilityForReviewDTOBuilder builder = VulnerabilityForReviewDTO.builder();
                builder.cve_id(v.getCveId())
                    .vuln_id(v.getVulnId())
                    .status_id("" + v.getStatusId())
                    .description(v.getDescription())
                    .cvss_scores(v.getCvssScores())
                    .vdos(v.getVdoCharacteristics())
                    .affected_releases(v.getAffectedReleases());

                for(VulnerabilityUpdate update: v.getUpdates()) {
                    builder.run_date_time(update.getDailyRunHistory().getRunDateTime());
                }
                return builder.build();
            }).toList());
        } else {
            List<Vulnerability> searchResults = vulnerabilityRepository.getVulnerabilitiesWithUpdateList(searchDate, crawled, rejected, accepted, reviewed);
            return ResponseEntity.status(HttpStatus.OK).body(searchResults.stream().map(v -> {
                VulnerabilityForReviewDTO.VulnerabilityForReviewDTOBuilder builder = VulnerabilityForReviewDTO.builder();
                builder.cve_id(v.getCveId())
                    .vuln_id(v.getVulnId())
                    .status_id("" + v.getStatusId())
                    .description(v.getDescription())
                    .cvss_scores(v.getCvssScores())
                    .vdos(v.getVdoCharacteristics())
                    .affected_releases(v.getAffectedReleases());

                for(VulnerabilityUpdate update: v.getUpdates()) {
                    builder.run_date_time(update.getDailyRunHistory().getRunDateTime());
                }
                return builder.build();
            }).toList());
        }
    }

    @PostMapping
    public ResponseEntity<String> createReview(
        @RequestParam(value="username") String userName,
        @RequestParam(value="token") String token,
        @RequestParam(value="vulnID") int vulnID,
        @RequestParam(value="cveId") String cveID,

        @RequestParam(value="complexUpdate", required=false) boolean complexUpdate,
        @RequestParam(value="atomicUpdate", required=false) boolean atomicUpdate,
        @RequestParam(value="updateDailyTable", required=false) boolean updateDailyTable,

        @RequestParam(value="statusID", required=false, defaultValue="1") int statusID,
        @RequestParam(value="info", required=false, defaultValue="") String info,
        @RequestParam(value="tweet", required=false, defaultValue="false") boolean isTweet,

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

       //Info needed for twitter
       String cveDescriptionTweet = null;

       if (atomicUpdate) {
           int userID = user.getUserID();

           reviewRepository.atomicUpdateVulnerability(statusID, vulnID, userID, cveID, info);

           if (statusID==4) {

               StringBuilder stringBuilder = new StringBuilder();
               BufferedReader bufferedReader = null;

               // try {
               //     bufferedReader = req.getReader();
               //     String line;
               //     while ((line = bufferedReader.readLine()) != null) {
               //         stringBuilder.append(line);
               //         stringBuilder.append(System.lineSeparator());
               //     }
               // } catch (IOException e) {
               //     e.printStackTrace();
               // }

               cveDescriptionTweet = stringBuilder.toString();

           }

       } else if (complexUpdate) {

           int userID = user.getUserID();

           // StringBuilder stringBuilder = new StringBuilder();
           // BufferedReader bufferedReader = null;

           // try {
           //     bufferedReader = req.getReader();
           //     String line;
           //     while ((line = bufferedReader.readLine()) != null) {
           //         stringBuilder.append(line);
           //         stringBuilder.append(System.lineSeparator());
           //     }
           // } catch (IOException e) {
           //     e.printStackTrace();
           // }

           // String dataString = stringBuilder.toString();
           // if (dataString == null)
           //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");

           JSONObject dataJSON = new JSONObject(updateData);

           String descriptionToUpdate = dataJSON.getString("descriptionToUpdate");

           String cveDescription = null;
           VDOupdateInfo vdoUpdate = null;
           CVSSupdate cvssUpdate = null;
           int[] productsToRemove = null;

           if (updateDescription) {
               cveDescription = dataJSON.getString("description");
           }

           if (updateVDO) {
               vdoUpdate = new VDOupdateInfo(dataJSON.getJSONObject("vdoUpdates"));
           }

           if (updateCVSS) {
               cvssUpdate = new CVSSupdate(dataJSON.getJSONObject("cvss"));
           }

           if (updateAffRel) {
               JSONArray jsonArray = dataJSON.getJSONArray("prodToRemove");
               productsToRemove = new int[jsonArray.length()];
               for (int i = 0; i < jsonArray.length(); i++) {
                   productsToRemove[i] = jsonArray.getInt(i);
               }
           }

           reviewRepository.complexUpdate(updateDescription, updateVDO, updateCVSS, updateAffRel, statusID, vulnID, userID, cveID, descriptionToUpdate, cveDescription, vdoUpdate, cvssUpdate,
                   productsToRemove);

       } else if (updateDailyTable) {
           int out = reviewRepository.updateDailyVulnerability(3);

           return ResponseEntity.status(HttpStatus.OK).body("" + out);
       }

       /**
        * Enable to tweet approved CVEs. <cveDescription> should be set to the approved
        * CVE description
        */
       if (isTweet && cveDescriptionTweet!=null && cveDescriptionTweet.length()>0) {
           TwitterApi twitterApi = new TwitterApi();
           twitterApi.postTweet(cveID, cveDescriptionTweet, false);
       }

       return ResponseEntity.status(HttpStatus.OK).body("");
   }
}
