package ru.semavin.TechRadarPolls.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "technologies")
public class Technology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long techId;

    @ManyToOne()
    @JoinColumn(name = "cat_id", referencedColumnName = "cat_id")
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "sec_id", referencedColumnName = "sec_id")
    private Section section;

    @ManyToOne()
    @JoinColumn(name = "ring_id", referencedColumnName = "ring_id")
    private Ring ring;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "stat_id", referencedColumnName = "stat_id")
    private TechnologyStatus status;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
