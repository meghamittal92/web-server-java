package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.request.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.request.UpdateCalorieRequest;
import com.client.calorieserver.domain.dto.response.AdminCalorieView;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.util.CalorieDTOSpecification;
import com.client.calorieserver.util.SpecificationBuilder;
import com.client.calorieserver.service.CalorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

/**
 * Controller to provide operations on all {@link Calorie}
 * entities.
 */
@RestController
@RequestMapping(path = "${server.request.path.calories}")
@RolesAllowed({Role.RoleConstants.ADMIN_VALUE})
@RequiredArgsConstructor
public class CalorieController {


    private final CalorieService calorieService;
    private final CalorieMapper calorieMapper;


    @GetMapping
    public Page<AdminCalorieView> findAll(@RequestParam(value = "search", required = false) String search, final Pageable pageable) {

        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();
        if (search != null)
            specBuilder = specBuilder.with(search);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);

        return calorieService.findAll(spec, pageable).map(calorieMapper::toAdminCalorieView);
    }

    @GetMapping(path = "/{id}")
    public AdminCalorieView find(@PathVariable("id") final Long calorieId) {

        return calorieMapper.toAdminCalorieView(calorieService.findById(calorieId));

    }

    @PostMapping(consumes = "application/json")
    public AdminCalorieView create(@RequestBody @Valid final CreateCalorieRequest createCalorieRequest) {

        Calorie calorie = calorieMapper.toCalorie(createCalorieRequest);
        return calorieMapper.toAdminCalorieView(calorieService.createCalorie(calorie));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") final Long calorieId) {

        calorieService.deleteById(calorieId);
    }

    @PatchMapping(path = "/{id}")
    public AdminCalorieView update(@PathVariable("id") final Long calorieId, @RequestBody @Valid final UpdateCalorieRequest updateCalorieRequest) {

        Calorie originalCalorie = calorieService.findById(calorieId);
        Calorie updatedCalorie = calorieMapper.updateCalorie(updateCalorieRequest, originalCalorie);

        return calorieMapper.toAdminCalorieView(calorieService.updateById(calorieId, updatedCalorie));
    }


}
