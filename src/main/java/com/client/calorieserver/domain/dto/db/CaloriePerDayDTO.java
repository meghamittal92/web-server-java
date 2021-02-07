package com.client.calorieserver.domain.dto.db;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "calories_per_day")
@Data
public class CaloriePerDayDTO {

    @EmbeddedId
    UserDay id;

    @Column(name = "total_calories")
    int totalCalories;

}
