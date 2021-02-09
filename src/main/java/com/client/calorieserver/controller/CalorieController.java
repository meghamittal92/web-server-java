package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.CalorieView;
import com.client.calorieserver.domain.dto.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.domain.model.search.CalorieSearchKey;
import com.client.calorieserver.domain.model.search.RelationalOperator;
import com.client.calorieserver.util.CalorieDTOSpecification;
import com.client.calorieserver.util.SpecificationBuilder;
import com.client.calorieserver.service.CalorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller to provide operations on {@link Calorie} for
 * a logged in user.
 */
@RestController
@RequestMapping(path = "/profile/calories")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class CalorieController {


    private final CalorieService calorieService;
    private final CalorieMapper calorieMapper;


    @GetMapping
    public Page<CalorieView> findAll(@RequestParam(value = "search", required = false) String search, final Pageable pageable) {

        final Long userId = fetchUserIdFromAuth();

        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>().with(CalorieSearchKey.userId.getName(), RelationalOperator.EQUAL, userId);
        if (search != null)
            specBuilder = specBuilder.with(search);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);

        return calorieService.findAll(spec, pageable).map(calorieMapper::toCalorieView);
    }

    @GetMapping(path = "/{id}")
    public CalorieView find(@PathVariable("id") final Long calorieId) {
        final Long userId = fetchUserIdFromAuth();
        return calorieMapper.toCalorieView(calorieService.findOneByUser(userId, calorieId));


    }

    @PostMapping(consumes = "application/json")
    public CalorieView create(@RequestBody @Valid final CreateCalorieRequest createCalorieRequest) {

        final Long userId = fetchUserIdFromAuth();
        Calorie calorie = calorieMapper.toCalorie(createCalorieRequest);
        return calorieMapper.toCalorieView(calorieService.createCalorieForUser(userId, calorie));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") final Long calorieId) {

        final Long userId = fetchUserIdFromAuth();
        calorieService.deleteById(userId, calorieId);
    }

    @PutMapping(path = "/{id}")
    public CalorieView replace(@PathVariable("id") final Long calorieId, @RequestBody @Valid final CreateCalorieRequest createCalorieRequest) {

        final Long userId = fetchUserIdFromAuth();
        Calorie updatedCalorie = calorieMapper.toCalorie(createCalorieRequest);
        return calorieMapper.toCalorieView(calorieService.replaceById(userId, calorieId, updatedCalorie));
    }

    private Long fetchUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((User) auth.getPrincipal()).getId();

    }

}
