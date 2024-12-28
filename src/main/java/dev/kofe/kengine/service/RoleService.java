package dev.kofe.kengine.service;

import dev.kofe.kengine.model.Role;

import java.util.List;

public interface RoleService {

    Role createRole(String roleName);
//    List<Role> getAllRoles();
    long count();
}
