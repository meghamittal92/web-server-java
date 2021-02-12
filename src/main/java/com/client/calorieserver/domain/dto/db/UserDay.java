package com.client.calorieserver.domain.dto.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDay implements Serializable {

	@Column(name = "user_id")
	Long userId;

	LocalDate date;

}
