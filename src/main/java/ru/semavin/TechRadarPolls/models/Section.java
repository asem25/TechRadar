package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sec_id;

    @Column(name = "sec_name", nullable = false)
    private String secName;
}
