package com.client.calorieserver.domain.dto.db;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


/**
 * Data class representing the User table in database
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class UserDTO extends  AuditableDTO<String>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(name = "expected_calories_per_day")
    private Integer expectedCaloriesPerDay;

    @Column(unique = true)
    private String email;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<RoleDTO> roleDTOs;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userDTO")
    private Set<CalorieDTO> calorieDTOS;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false, name = "user_id", referencedColumnName = "id")
    private List<CaloriePerDayDTO> caloriePerDayDTOs;

}