package org.nvip.api.controllers;

import org.nvip.api.serializers.VulnerabilityForReviewDTO;
import org.nvip.data.repositories.ReviewRepository;
import org.nvip.data.repositories.UserRepository;
import org.nvip.data.repositories.VulnerabilityRepository;
import org.nvip.entities.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

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


        if (cveID != null) {
           VulnerabilityDetails vulnDetails = reviewRepository.getVulnerabilityDetails(cveID);
           if(vulnDetails != null){
               VulnerabilityForReviewDTO.VulnerabilityForReviewDTOBuilder builder = VulnerabilityForReviewDTO.builder();
               builder.cve_id(vulnDetails.getCve_id())
                    .status_id(vulnDetails.getStatus_id())
                    .description(vulnDetails.getDescription())
                    .run_date_time(LocalDateTime.parse(vulnDetails.getRun_date_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(builder.build())) ;
            }
        } else {
            List<Vulnerability> searchResults = vulnerabilityRepository.getVulnerabilitiesWithUpdateList(searchDate, crawled, rejected, accepted, reviewed);
            return ResponseEntity.status(HttpStatus.OK).body(searchResults.stream().map(v -> {
                VulnerabilityForReviewDTO.VulnerabilityForReviewDTOBuilder builder = VulnerabilityForReviewDTO.builder();
                builder.cve_id(v.getCveId())
                    .vuln_id(v.getVulnId())
//                    .status_id(v.getStatus())
                    .description(v.getDescription());

                for(VulnerabilityUpdate update: v.getUpdates()) {
                    builder.run_date_time(update.getDailyRunHistory().getRunDateTime());
                }
                return builder.build();
            }).toList());
        }
        return null;
    }

//    @PostMapping
//    public String createReview(@RequestParam(value="cveId", defaultValue = "") String cveId) {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
//        Gson gson = gsonBuilder.setPrettyPrinting().create();
//
//        boolean complexUpdate = Boolean.parseBoolean(req.getParameter("complexUpdate"));
//        boolean atomicUpdate = Boolean.parseBoolean(req.getParameter("atomicUpdate"));
//        boolean updateDailyTable = Boolean.parseBoolean(req.getParameter("updateDailyTable"));
//
//        String userName = req.getParameter("username");
//        String token = req.getParameter("token");
//
////        TODO: Spring Security
////        if (userName == null || token == null)
////            ServletUtil.setResponse(resp, 401, "Unauthorized user!");
////
////        User user = userRepository.getRoleIDandExpirationDate(userName, token);
////
////        if (user == null || user.getRoleId() < 1 || user.getRoleId() > 2)
////            ServletUtil.setResponse(resp, 401, "Unauthorized user!");
//
//        //Info needed for twitter
//        boolean isTweet = false;
//        String cveDescriptionTweet = null;
//
//        String cveID = req.getParameter("cveID");
//
//        if (atomicUpdate) {
//            int statusID = Integer.parseInt(req.getParameter("statusID"));
//            int userID = user.getUserID();
//
//            int vulnID = Integer.parseInt(req.getParameter("vulnID"));
//            String info = req.getParameter("info");
//            reviewRepository.atomicUpdateVulnerability(statusID, vulnID, userID, cveID, info);
//
//            if (statusID==4) {
//
//                isTweet = Boolean.parseBoolean(req.getParameter("tweet"));
//                StringBuilder stringBuilder = new StringBuilder();
//                BufferedReader bufferedReader = null;
//
//                try {
//                    bufferedReader = req.getReader();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(line);
//                        stringBuilder.append(System.lineSeparator());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                cveDescriptionTweet = stringBuilder.toString();
//
//            }
//
//        } else if (complexUpdate) {
//
//            boolean updateDescription = Boolean.parseBoolean(req.getParameter("updateDescription"));
//            boolean updateVDO = Boolean.parseBoolean(req.getParameter("updateVDO"));
//            boolean updateCVSS = Boolean.parseBoolean(req.getParameter("updateCVSS"));
//            boolean updateAffRel = Boolean.parseBoolean(req.getParameter("updateAffRel"));
//
//            int statusID = Integer.parseInt(req.getParameter("statusID"));
//            int userID = user.getUserID();
//            int vulnID = Integer.parseInt(req.getParameter("vulnID"));
//
//            StringBuilder stringBuilder = new StringBuilder();
//            BufferedReader bufferedReader = null;
//
//            try {
//                bufferedReader = req.getReader();
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(line);
//                    stringBuilder.append(System.lineSeparator());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String dataString = stringBuilder.toString();
//            if (dataString == null)
//                return;
//
//            JSONObject dataJSON = new JSONObject(dataString);
//
//            String descriptionToUpdate = dataJSON.getString("descriptionToUpdate");
//
//            String cveDescription = null;
//            VDOupdateInfo vdoUpdate = null;
//            CVSSupdate cvssUpdate = null;
//            int[] productsToRemove = null;
//
//            if (updateDescription) {
//                cveDescription = dataJSON.getString("description");
//            }
//
//            if (updateVDO) {
//                vdoUpdate = new VDOupdateInfo(dataJSON.getJSONObject("vdoUpdates"));
//            }
//
//            if (updateCVSS) {
//                cvssUpdate = new CVSSupdate(dataJSON.getJSONObject("cvss"));
//            }
//
//            if (updateAffRel) {
//                JSONArray jsonArray = dataJSON.getJSONArray("prodToRemove");
//                productsToRemove = new int[jsonArray.length()];
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    productsToRemove[i] = jsonArray.getInt(i);
//                }
//            }
//
//            reviewRepository.complexUpdate(updateDescription, updateVDO, updateCVSS, updateAffRel, statusID, vulnID, userID, cveID, descriptionToUpdate, cveDescription, vdoUpdate, cvssUpdate,
//                    productsToRemove);
//
//        } else if (updateDailyTable) {
//            int out = reviewRepository.updateDailyVulnerability(3);
//
//            try {
//                resp.setContentType("text/html");
//                resp.setCharacterEncoding("UTF-8");
//                resp.getWriter().write(Integer.toString(out));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * Enable to tweet approved CVEs. <cveDescription> should be set to the approved
//         * CVE description
//         */
//        if (isTweet && cveDescriptionTweet!=null && cveDescriptionTweet.length()>0) {
//            TwitterApi twitterApi = new TwitterApi();
//            twitterApi.postTweet(cveID, cveDescriptionTweet, false);
//        }
//
//        return "";
//    }
}
