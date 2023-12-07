/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.nvip.api.services;

import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.CreateUserDTO;
import org.nvip.api.serializers.CredentialsDTO;
import org.nvip.api.serializers.UserDTO;
import org.nvip.data.repositories.UserRepository;
import org.nvip.entities.User;
import org.nvip.util.AppException;
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
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleId(user.getRoleId())
                .expirationDate(user.getExpirationDate())
                .build();
    }

    public UserDTO login(CredentialsDTO credentials) {
        User user = userRepository.findByUserName(credentials.getUserName())
                .orElseThrow(() -> new AppException("Invalid Username or Password.", HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPasswordHash()))
            throw new AppException("Invalid Username or Password.", HttpStatus.NOT_FOUND);
        return toDTO(user);
    }

    public UserDTO createUser(CreateUserDTO userData) {
        boolean alreadyExists = userRepository.findByUserName(userData.getUsername()).isPresent();
        if (alreadyExists) throw new AppException(String.format("User %s already exists.", userData.getUsername()), HttpStatus.FORBIDDEN);
        User newUser = new User(null, userData.getUsername(), userData.getFname(), userData.getLname(), userData.getEmail(), 2);
        // encode password using BCryptPasswordEncoder
        char[] passwordToEncode = userData.getPassword();
        newUser.setPasswordHash(passwordEncoder.encode(CharBuffer.wrap(passwordToEncode)));
        // save entity
        userRepository.save(newUser);
        return toDTO(newUser);
    }
}
