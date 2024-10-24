package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "role_name", nullable = false)
    private String roleName;
}
