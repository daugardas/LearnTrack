package com.learntrack.authorizationserver.repositories;

import com.learntrack.authorizationserver.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);

    List<Role> findByAuthoritiesIsEmpty();

    boolean existsByName(String name);
}
