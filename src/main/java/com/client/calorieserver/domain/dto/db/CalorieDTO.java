package com.client.calorieserver.domain.dto.db;

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
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
    private Integer numCalories;

    @Column(name = "meal_details")
    private String mealDetails;


    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", referencedColumnName = "id")
    private UserDTO userDTO;

    @ManyToOne(cascade = CascadeType.REFRESH, optional = false, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas(
            {
                    @JoinColumnOrFormula(column = @JoinColumn(insertable = false, updatable = false, name = "user_id", referencedColumnName = "user_id")),
                    @JoinColumnOrFormula(formula = @JoinFormula(value = "CAST(datetime as date)", referencedColumnName = "date"))
            }
    )
    private CaloriePerDayDTO caloriePerDayDTO;

}

