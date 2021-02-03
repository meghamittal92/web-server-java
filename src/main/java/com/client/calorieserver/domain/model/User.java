package com.client.calorieserver.domain.model;


import lombok.Data;

import javax.persistence.*;


/**
 * Data class representing the User table in database
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private boolean enabled;


}