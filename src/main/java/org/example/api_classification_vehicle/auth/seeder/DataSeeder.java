package org.example.api_classification_vehicle.auth.seeder;
import org.example.api_classification_vehicle.auth.model.AppUser;
import org.example.api_classification_vehicle.auth.model.ERole;
import org.example.api_classification_vehicle.auth.model.Role;
import org.example.api_classification_vehicle.auth.repository.RoleRepository;
import org.example.api_classification_vehicle.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        for (ERole roleName : ERole.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("Created role: " + roleName);
            }
        }
    }

    private void seedAdminUser() {
        String adminUsername = "admin";
        String adminEmail = "admin@homintec.com";
        String adminPassword = "Admin123!";

        if (!userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            AppUser admin = new AppUser();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Collections.singleton(adminRole));

            userRepository.save(admin);
            System.out.println("Created admin user: " + adminUsername);
        }
    }
}