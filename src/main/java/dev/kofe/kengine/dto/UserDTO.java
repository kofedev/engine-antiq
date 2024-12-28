package dev.kofe.kengine.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String email;
    private String password;
    private String emailToken;
    private Boolean confirmed;
    private Boolean initial;
    private List<String> roles;
}
