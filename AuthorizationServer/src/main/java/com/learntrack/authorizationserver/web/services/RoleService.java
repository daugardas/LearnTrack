package com.learntrack.authorizationserver.web.services;

import com.learntrack.authorizationserver.models.Role;
import com.learntrack.authorizationserver.models.User;
import com.learntrack.authorizationserver.repositories.RoleRepository;
import com.learntrack.authorizationserver.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Iterable<Role> findAll() {
        return roleRepository.findAll();
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public Role update(Long id, Role role) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Role not found");
        }
        Role existingRole = roleOptional.get();
        existingRole.setName(role.getName());
        return save(existingRole);
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public User addRoleToUser(Long userId, Long roleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Role not found");
        }
        User user = userOptional.get();
        Role role = roleOptional.get();
        user.addRole(role);
        return userRepository.save(user);
    }

    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Role not found");
        }

        User user = userOptional.get();
        Role role = roleOptional.get();
        user.removeRole(role);
        return userRepository.save(user);
    }

    public Iterable<Role> findAllRolesForUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return userOptional.get().getRoles();
    }


}
