package com.client.calorieserver.domain.dto.db;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calories")
@Data
public class CalorieDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datetime")
    private LocalDateTime dateTime;

    @Column(name = "num_calories")
    private int numCalories;

    @Column(name = "details")
    private String details;

    @Column(name = "is_within_limit")
    private boolean isWithinLimit;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", referencedColumnName = "id")
    private UserDTO userDTO;


}

