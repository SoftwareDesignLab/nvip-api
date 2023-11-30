package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class UserDTOTest {

    LocalDateTime oldtime = LocalDateTime.of(LocalDate.of(Integer.parseInt("2018"), Integer.parseInt("3"), Integer.parseInt("4")), LocalTime.of(Integer.parseInt("3"), Integer.parseInt("3"), Integer.parseInt("4")));
    LocalDateTime newTime = LocalDateTime.of(LocalDate.of(Integer.parseInt("2020"), Integer.parseInt("3"), Integer.parseInt("4")), LocalTime.of(Integer.parseInt("3"), Integer.parseInt("3"), Integer.parseInt("4")));
    @Test
    public void testGettersAndSetters() {
        UserDTO userDTO = new  UserDTO(0,"old token","old userName", 0,oldtime);
        userDTO.setUserID(1);
        userDTO.setToken("new tokenz");
        userDTO.setUserName("new UserName");
        userDTO.setExpirationDate(newTime);
        userDTO.setRoleId(1);



        assertEquals(1,  userDTO.getUserID());
        assertEquals("new tokenz",     userDTO.getToken());
        assertEquals("new UserName" ,  userDTO.getUserName());
        assertEquals(newTime, userDTO.getExpirationDate());
        assertEquals(1, userDTO.getRoleId());
    }

    @Test
    public void testBuilder() {
        UserDTO userDTO = UserDTO.builder()
                .userID(1)
                .token("new token")
                .roleId(1)
                .expirationDate(newTime)
                .userName("new UserName").build();

        assertEquals(1,  userDTO.getUserID());
        assertEquals("new token",     userDTO.getToken());
        assertEquals("new UserName" ,  userDTO.getUserName());
        assertEquals(newTime, userDTO.getExpirationDate());
        assertEquals(1, userDTO.getRoleId());
    }
}
