package org.nvip.api.controllers;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.CreateUserDTO;
import org.nvip.api.serializers.CredentialsDTO;
import org.nvip.api.serializers.UserDTO;
import org.nvip.data.repositories.UserRepository;
import org.nvip.entities.User;
import org.nvip.util.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {

//    final UserRepository userRepository;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody CredentialsDTO credentials) {
        UserDTO user = userService.login(credentials);
        return ResponseEntity.ok(user);
        //        return UserDTO.builder()
//                .userID(user.getUserID())
//                .token(user.getToken())
//                .userName(user.getUserName())
//                .roleId(user.getRoleId())
//                .expirationDate(user.getExpirationDate())
//                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO userData) {
        UserDTO user = userService.createUser(userData);
        return ResponseEntity.ok(user);
//        String userName = userData.get("username").toLowerCase();
//        String password = userData.get("password");
//        String fname = userData.get("fname");
//        String lname = userData.get("lname");
//        String email = userData.get("email");
//
//        User user = new User(null, userName, password, fname, lname, email, 2);
//
////        int rs = userRepository.createUser(user, password);

//        int rs = 0;
//        if (rs == -2)
//            return new ResponseEntity<>("User already exists!", HttpStatus.BAD_REQUEST);
//
//        if (rs == -1)
//            return new ResponseEntity<>("Something is wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
//
//        return new ResponseEntity<>(HttpStatus.OK);
    }
}
