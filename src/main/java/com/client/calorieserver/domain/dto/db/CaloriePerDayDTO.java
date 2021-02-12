package com.client.calorieserver.domain.dto.db;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "calories_per_day")
@Getter
@Setter
public class CaloriePerDayDTO extends AuditableDTO<String> {

	@EmbeddedId
	UserDay id;

	@Column(name = "total_calories")
	Integer totalCalories;

}
