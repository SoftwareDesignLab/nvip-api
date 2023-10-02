package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateUserDTO {
    private String username;
    private char[] password;
    private String fname;
    private String lname;
    private String email;
}
