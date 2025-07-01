package org.example.api_classification_vehicle.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.api_classification_vehicle.Audit;

@Entity
@Table(name = "roles")
@Data
public class Role extends Audit {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    // Constructeurs, getters, setters...
}

