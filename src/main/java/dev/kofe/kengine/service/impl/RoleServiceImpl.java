package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.model.Role;
import dev.kofe.kengine.repository.RoleRepository;
import dev.kofe.kengine.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(String roleName) {
        return roleRepository.save(new Role(roleName));
    }

    @Override
    public long count() {
        return roleRepository.count();
    };

}
