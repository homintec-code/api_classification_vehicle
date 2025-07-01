package org.example.api_classification_vehicle.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.api_classification_vehicle.Audit;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class AppUser  extends Audit {

    @Column(unique = true, nullable = false)
    private String username;
    private String email;

    @Column(nullable = false)
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public AppUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;


    }

    public AppUser() {

    }

    // Constructeurs, getters, setters...
}