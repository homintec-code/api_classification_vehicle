package org.example.api_classification_vehicle.auth.controller;

import org.example.api_classification_vehicle.auth.dto.*;
import org.example.api_classification_vehicle.auth.model.AppUser;
import org.example.api_classification_vehicle.auth.model.ERole;
import org.example.api_classification_vehicle.auth.model.Role;
import org.example.api_classification_vehicle.auth.repository.RoleRepository;
import org.example.api_classification_vehicle.auth.repository.UserRepository;
import org.example.api_classification_vehicle.auth.service.UserDetailsImpl;
import org.example.api_classification_vehicle.auth.service.UserService;
import org.example.api_classification_vehicle.config.JwtUtil;
import org.example.api_classification_vehicle.config.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final   AuthenticationManager authenticationManager;

    private final   UserRepository userRepository;

    private final  PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserService userService;


    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils, RoleRepository roleRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }


    @PostMapping("/signin")
    public SigninResponseDto login(@RequestBody LoginRequest authenticateUserDto) {
        return userService.login(authenticateUserDto.getUsername(), authenticateUserDto.getPassword());
    }
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupRequest signUpRequest) throws Throwable {
        ResponseEntity<MessageResponse> registeredUser = userService.create(signUpRequest);
        return ResponseEntity.ok(registeredUser.getBody());
    }
}