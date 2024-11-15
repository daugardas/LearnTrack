package com.learntrack.authorizationserver.services;

import com.learntrack.authorizationserver.exceptions.UserAlreadyExistsException;
import com.learntrack.authorizationserver.models.Role;
import com.learntrack.authorizationserver.models.User;
import com.learntrack.authorizationserver.models.UserRegistrationRequestDTO;
import com.learntrack.authorizationserver.models.UserResponseDTO;
import com.learntrack.authorizationserver.repositories.RoleRepository;
import com.learntrack.authorizationserver.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

@Service
public class UserRegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationRequestDTO userRegDTO) {
        User savedUSer;
        try {
            savedUSer = createUser(userRegDTO);
        } catch (RoleNotFoundException e) {
            throw new RuntimeException(e);
        }
        return UserResponseDTO.from(savedUSer);
    }

    private User createUser(UserRegistrationRequestDTO userRegDTO) throws RoleNotFoundException {
        Optional<User> existingUser = userRepository.findByUsername(userRegDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        Optional<Role> role = roleRepository.findByName(userRegDTO.getRole());
        if (!role.isPresent()) {
            throw new RoleNotFoundException("Role not found");
        }

        User user = new User();
        user.setUsername(userRegDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegDTO.getPassword()));

        Role userRole = role.get();
        user.addRole(userRole);

        return userRepository.save(user);
    }

}
