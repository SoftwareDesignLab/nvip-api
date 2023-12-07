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

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nvip.api.serializers.CreateUserDTO;
import org.nvip.api.serializers.CredentialsDTO;
import org.nvip.api.serializers.UserDTO;
import org.nvip.data.repositories.UserRepository;
import org.nvip.entities.User;
import org.nvip.util.AppException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void toDTO() {
        UserService userService = new UserService(userRepository, passwordEncoder);
        User user = new User(null, "username", "fname", "lname", "email", 2);
        user.setUserID(1);
        user.setToken("token");
        LocalDateTime today = LocalDateTime.now();
        user.setExpirationDate(today);
        UserDTO userDTO = userService.toDTO(user);
        assertEquals(1, userDTO.getUserID());
        assertEquals("token", userDTO.getToken());
        assertEquals("username", userDTO.getUserName());
        assertEquals("fname", userDTO.getFirstName());
        assertEquals("lname", userDTO.getLastName());
        assertEquals(2, userDTO.getRoleId());
        assertEquals(today, userDTO.getExpirationDate());
    }

    @Test
    void testLoginSuccessful() {
        CredentialsDTO cred = CredentialsDTO.builder()
                .userName("gooduser")
                .password("goodpassword".toCharArray())
                .build();
        User user = new User();
        when(userRepository.findByUserName(cred.getUserName())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(any(CharBuffer.class), any())).thenReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder);
        UserDTO userDTO = userService.login(cred);
        assertNotNull(userDTO);
    }

    @Test
    void testLoginInvalidUsernameAppException() {
        CredentialsDTO cred = CredentialsDTO.builder()
                .userName("invalidUser")
                .password("password".toCharArray())
                .build();
        when(userRepository.findByUserName(cred.getUserName())).thenReturn(java.util.Optional.empty());
        assertThrows(AppException.class, () -> {
            UserService userService = new UserService(userRepository, passwordEncoder);
            userService.login(cred);
        });
    }

    @Test
    void testLoginInvalidPasswordAppException() {
        CredentialsDTO cred = CredentialsDTO.builder()
                .userName("user")
                .password("invalidPass".toCharArray())
                .build();
        User user = new User();
        when(userRepository.findByUserName(cred.getUserName())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(any(CharBuffer.class), any())).thenReturn(false);
        assertThrows(AppException.class, () -> {
            UserService userService = new UserService(userRepository, passwordEncoder);
            userService.login(cred);
        });
    }

    @Test
    void createUserAlreadyExists() {
        CreateUserDTO userData = CreateUserDTO.builder()
                .username("user")
                .fname("fname")
                .lname("lname")
                .email("email")
                .password("password".toCharArray())
                .build();
        when(userRepository.findByUserName(userData.getUsername())).thenReturn(java.util.Optional.of(new User()));
        assertThrows(AppException.class, () -> {
            UserService userService = new UserService(userRepository, passwordEncoder);
            userService.createUser(userData);
        });
    }

    @Test
    void createUserDoesNotAlreadyExist() {
        CreateUserDTO userData = CreateUserDTO.builder()
                .username("user")
                .fname("fname")
                .lname("lname")
                .email("email")
                .password("password".toCharArray())
                .build();
        when(userRepository.findByUserName(userData.getUsername())).thenReturn(java.util.Optional.empty());
        when(passwordEncoder.encode(any(CharBuffer.class))).thenReturn("encodedPassword");
        UserService userService = new UserService(userRepository, passwordEncoder);
        UserDTO userDTO = userService.createUser(userData);
        assertEquals("user", userDTO.getUserName());
        assertEquals("fname", userDTO.getFirstName());
        assertEquals("lname", userDTO.getLastName());
        assertEquals(2, userDTO.getRoleId());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(any(CharBuffer.class));
        verify(userRepository, times(1)).findByUserName(any(String.class));

    }
}