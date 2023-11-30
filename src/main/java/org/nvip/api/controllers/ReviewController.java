package org.nvip.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.VdoUpdate;
import org.nvip.api.services.ReviewService;
import org.nvip.data.repositories.UserRepository;

import org.nvip.entities.VdoCharacteristic;
import org.nvip.util.AppException;
import org.nvip.util.CvssGenUtil;
import org.nvip.util.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    final UserRepository userRepository;
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
//        @RequestBody VdoUpdate updateData
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

        reviewService.complexUpdate(userID, userName, cveID, cveDescription, vdoUpdate,
                productsToRemove);


        //SEND MESSAGE TO RABBITMQ
        Messenger.sendCveId(cveID);

       return ResponseEntity.status(HttpStatus.OK).body("");
   }
}
