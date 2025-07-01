package org.example.api_classification_vehicle.auth.service;

import org.example.api_classification_vehicle.auth.dto.MessageResponse;
import org.example.api_classification_vehicle.auth.dto.SigninResponseDto;
import org.example.api_classification_vehicle.auth.dto.SignupRequest;
import org.example.api_classification_vehicle.auth.model.AppUser;
import org.example.api_classification_vehicle.auth.model.ERole;
import org.example.api_classification_vehicle.auth.model.Role;
import org.example.api_classification_vehicle.auth.repository.RoleRepository;
import org.example.api_classification_vehicle.auth.repository.UserRepository;
import org.example.api_classification_vehicle.config.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtils jwtUtils, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }


    // Use passwordEncoder.encode() in your methods

    public SigninResponseDto login(String username, String password) {

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        // Check if the provided password matches the stored password
        if (!this.checkPassword(user, password)) {
            throw new IllegalArgumentException("Invalid password");
        }
        String accessToken = jwtUtils.generateToken(user);
        return new SigninResponseDto(accessToken, user);
    }

    public boolean checkPassword(AppUser userInfo, String rawPassword) {
        // Compare the raw password entered by the user with the encoded password stored in DB
        return passwordEncoder.matches(rawPassword, userInfo.getPassword());
    }



    // Create a new user
    public ResponseEntity<MessageResponse> create(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        AppUser user = new AppUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {

            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }


}