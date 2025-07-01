package org.example.api_classification_vehicle.auth.repository;
import org.example.api_classification_vehicle.auth.model.ERole;
import org.example.api_classification_vehicle.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    boolean existsByName(ERole roleName);
    ////  Optional<Role> findByName(String name);
}
