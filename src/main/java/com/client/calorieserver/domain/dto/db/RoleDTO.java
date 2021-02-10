package com.client.calorieserver.domain.dto.db;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleDTO extends  AuditableDTO<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roleDTOs")
    private Collection<UserDTO> userDTOS;

}
