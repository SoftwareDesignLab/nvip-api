package org.nvip.api.controllers;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.api.services.ReviewService;
import org.nvip.data.repositories.UserRepository;
import org.nvip.data.repositories.VulnerabilityRepository;

import org.nvip.util.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.json.JSONObject;
import org.json.JSONArray;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    final UserRepository userRepository;
    final VulnerabilityRepository vulnerabilityRepository;
    final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<String> createReview(
        @RequestParam(value="username") String userName,
        @RequestParam(value="token") String token,
        @RequestParam(value="cveId") String cveID,
        @RequestParam(value="updateDescription", required=false, defaultValue="false") boolean updateDescription,
        @RequestParam(value="updateVDO", required=false, defaultValue="false") boolean updateVDO,
        @RequestParam(value="updateAffRel", required=false, defaultValue="false") boolean updateAffRel,
        @RequestBody String updateData
    ) 
    {

        if (userName == null || token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        int userID = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(String.format("Unable to find admin user %s... Change not made", userName), HttpStatus.FORBIDDEN))
                .getUserID();

        JSONObject dataJSON = new JSONObject(updateData);
        String cveDescription = null;
        VdoUpdate vdoUpdate = null;
        int[] productsToRemove = null;

        if (updateDescription) {
            cveDescription = dataJSON.getString("description");
        }

        if (updateVDO) {
            vdoUpdate = new VdoUpdate(dataJSON.getJSONObject("vdoUpdates"));
        }

        if (updateAffRel) {
            JSONArray jsonArray = dataJSON.getJSONArray("prodToRemove");
            productsToRemove = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                productsToRemove[i] = jsonArray.getInt(i);
            }
        }

        reviewService.complexUpdate(updateDescription, updateVDO, updateAffRel, userID, userName, cveID, cveDescription, vdoUpdate,
                productsToRemove);

        return ResponseEntity.status(HttpStatus.OK).body("");
   }
}
