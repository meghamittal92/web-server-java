package com.client.calorieserver.domain.dto.db;


import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.util.Set;


/**
 * Data class representing the User table in database
 */
@Entity
@Table(name = "users")
@Data
public class UserDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(name = "expected_calories_per_day")
    private Integer expectedCaloriesPerDay;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<RoleDTO> roleDTOs;

    @OneToMany(mappedBy = "userDTO")
    private Set<CalorieDTO> calorieDTOS;

}