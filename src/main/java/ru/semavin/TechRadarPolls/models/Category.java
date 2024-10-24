package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cat_id;

    @Column(name = "cat_name", nullable = false)
    private String roleName;
}
