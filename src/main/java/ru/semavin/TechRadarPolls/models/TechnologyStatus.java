package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;

import lombok.Data;


@Data
@Entity

@Table(name = "statuses")
public class TechnologyStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stat_id;

    @Column(name = "stat_name", nullable = false)
    private String status;
}
