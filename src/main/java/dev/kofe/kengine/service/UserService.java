package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.User;

public interface UserService {
    User loadUserByEmail(String email);
    User createUser(String email, String password);
    void assignRoleToUser(String email, String roleName);
    UserDTO changePassword(UserDTO userDTO);
    UserDTO changeEmail(UserDTO userDTO);
    UserDTO changeSingleRole(UserDTO userDTO);
    void sendConfirmationEmail (User user);
}
