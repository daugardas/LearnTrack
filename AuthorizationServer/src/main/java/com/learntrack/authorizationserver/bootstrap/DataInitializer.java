package com.learntrack.authorizationserver.bootstrap;

import com.learntrack.authorizationserver.models.Authority;
import com.learntrack.authorizationserver.models.Role;
import com.learntrack.authorizationserver.models.User;
import com.learntrack.authorizationserver.repositories.AuthorityRepository;
import com.learntrack.authorizationserver.repositories.RoleRepository;
import com.learntrack.authorizationserver.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AuthorityRepository authorityRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Initializing data...");

        Authority readAuthority = createAuthorityIfNotFound("READ");
        Authority writeAuthority = createAuthorityIfNotFound("WRITE");
        Authority deleteAuthority = createAuthorityIfNotFound("DELETE");

        Role userRole = createRoleIfNotFound("ROLE_USER");
        Role lecturerRole = createRoleIfNotFound("ROLE_LECTURER");
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");

        userRole.addAuthority(readAuthority);
        userRole.addAuthority(writeAuthority);

        lecturerRole.addAuthority(readAuthority);
        lecturerRole.addAuthority(writeAuthority);

        adminRole.addAuthority(readAuthority);
        adminRole.addAuthority(writeAuthority);
        adminRole.addAuthority(deleteAuthority);

        roleRepository.save(userRole);
        roleRepository.save(lecturerRole);
        roleRepository.save(adminRole);

        // create default users if they don't exist
        if(!userRepository.existsByUsername("admin")) {
            User adminUser = new User("admin", passwordEncoder.encode("password"));
            adminUser.addRole(adminRole);
            userRepository.save(adminUser);
        }

        if(!userRepository.existsByUsername("user")) {
            User userUser = new User("user", passwordEncoder.encode("password"));
            userUser.addRole(userRole);
            userRepository.save(userUser);
        }

        if(!userRepository.existsByUsername("lecturer")) {
            User lecturerUser = new User("lecturer", passwordEncoder.encode("password"));
            lecturerUser.addRole(lecturerRole);
            userRepository.save(lecturerUser);
        }

        logger.info("Data initialized.");
    }

    private Authority createAuthorityIfNotFound(String name) {
        Optional<Authority> authority = authorityRepository.findByName(name);
        if (authority.isPresent()) {
            logger.info("Authority already exists: {}", name);
            return authority.get();
        }
        logger.info("Creating authority: {}", name);
        return authorityRepository.save(new Authority(name));
    }

    private Role createRoleIfNotFound(String name) {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()) {
            logger.info("Role already exists: {}", name);
            return role.get();
        }
        logger.info("Creating role: {}", name);
        return roleRepository.save(new Role(name));
    }


}
