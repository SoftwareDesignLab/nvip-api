package org.nvip.api.controllers;

import org.nvip.api.serializers.UserDTO;
import org.nvip.data.repositories.UserRepository;
import org.nvip.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value="/login")
public class LoginController {

    final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public UserDTO login(
            @RequestParam(value="userName", required = false) String userName,
            @RequestParam(value="passwordHash", required = false) String passwordHash
    ) {
        User user = userRepository.login(userName, passwordHash);
        return UserDTO.builder()
                .userID(user.getUserID())
                .token(user.getToken())
                .userName(user.getUserName())
                .roleId(user.getRoleId())
                .expirationDate(user.getExpirationDate())
                .build();
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody Map<String, String> userData) {
        String userName = userData.get("username").toLowerCase();
        String password = userData.get("password");
        String fname = userData.get("fname");
        String lname = userData.get("lname");
        String email = userData.get("email");

        User user = new User(null, userName, password, fname, lname, email, 2);

        int rs = userRepository.createUser(user, password);

        if (rs == -2)
            return new ResponseEntity<>("User already exists!", HttpStatus.BAD_REQUEST);

        if (rs == -1)
            return new ResponseEntity<>("Something is wrong!", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
