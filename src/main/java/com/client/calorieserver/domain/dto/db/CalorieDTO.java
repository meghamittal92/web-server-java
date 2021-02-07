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

    @Column(name = "meal_details")
    private String mealDetails;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", referencedColumnName = "id")
    private UserDTO userDTO;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns(
            {
                    @JoinColumn(insertable = false, updatable = false, name = "user_id", referencedColumnName = "user_id"),
                    @JoinColumn(insertable = false, updatable = false, name = "datetime", referencedColumnName = "date"),

            })
    private CaloriePerDayDTO caloriePerDayDTO;

}

