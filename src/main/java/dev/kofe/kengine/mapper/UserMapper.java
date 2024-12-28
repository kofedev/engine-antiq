package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.Role;
import dev.kofe.kengine.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserMapper {

    public UserDTO fromUser(User user) {
        if (user == null) return null;
        UserDTO userDTO = new UserDTO();
        userDTO.setRoles(new ArrayList<>());
        BeanUtils.copyProperties(user, userDTO);
        for (Role role : user.getRoles()) {
            userDTO.getRoles().add(role.getName());
        }

        return userDTO;
    }

    public User fromUserDTO(UserDTO userDTO) {
        if (userDTO == null) return null;
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

}
