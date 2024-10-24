package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rings")
public class Ring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ring_id;

    @Column(name = "ring_name", nullable = false)
    private String ring_name;
}
