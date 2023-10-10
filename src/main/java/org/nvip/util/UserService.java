package org.nvip.util;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.CreateUserDTO;
import org.nvip.api.serializers.CredentialsDTO;
import org.nvip.api.serializers.UserDTO;
import org.nvip.data.repositories.UserRepository;
import org.nvip.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserDTO toDTO(User user) {
        return UserDTO.builder()
                .userID(user.getUserID())
                .token(user.getToken())
                .userName(user.getUserName())
                .roleId(user.getRoleId())
                .expirationDate(user.getExpirationDate())
                .build();
    }

    public UserDTO login(CredentialsDTO credentials) {
        User user = userRepository.findByUserName(credentials.getUserName());
        if (user == null)
            throw new AppException("Invalid Username or Password.", HttpStatus.NOT_FOUND);
        if (!passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPasswordHash()))
            throw new AppException("Invalid Username or Password.", HttpStatus.NOT_FOUND);
        return toDTO(user);
    }

    public UserDTO createUser(CreateUserDTO userData) {
        User user = userRepository.findByUserName(userData.getUsername());
        if (user != null) {
            throw new AppException(String.format("User %s already exists.", user.getUserName()), HttpStatus.FORBIDDEN);
        }
        User newUser = new User(null, userData.getUsername(), userData.getFname(), userData.getLname(), userData.getEmail(), 2);
        // encode password using BCryptPasswordEncoder
        char[] passwordToEncode = userData.getPassword();
        newUser.setPasswordHash(passwordEncoder.encode(CharBuffer.wrap(passwordToEncode)));
        // save entity
        newUser = userRepository.save(newUser);
        return toDTO(newUser);
    }
}
