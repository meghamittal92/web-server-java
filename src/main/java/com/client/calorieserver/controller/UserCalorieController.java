package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.UserCalorieView;
import com.client.calorieserver.domain.dto.CreateUserCalorieRequest;
import com.client.calorieserver.domain.dto.UpdateCalorieRequest;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.Role;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

/**
 * Controller to provide operations on {@link Calorie} for
 * a logged in user.
 */
@RestController
@RequestMapping(path = "api/v1/profile/calories")
@RolesAllowed({Role.RoleConstants.USER_VALUE})
@RequiredArgsConstructor
public class UserCalorieController {


    private final CalorieService calorieService;
    private final CalorieMapper calorieMapper;


    @GetMapping
    public Page<UserCalorieView> findAll(@RequestParam(value = "search", required = false) String search, final Pageable pageable) {

        final Long userId = fetchUserIdFromAuth();

        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>().with(CalorieSearchKey.userId.getName(), RelationalOperator.EQUAL, userId);
        if (search != null)
            specBuilder = specBuilder.with(search);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);

        return calorieService.findAll(spec, pageable).map(calorieMapper::toUserCalorieView);
    }

    @GetMapping(path = "/{id}")
    public UserCalorieView find(@PathVariable("id") final Long calorieId) {
        final Long userId = fetchUserIdFromAuth();
        return calorieMapper.toUserCalorieView(calorieService.findOneByUser(userId, calorieId));


    }

    @PostMapping(consumes = "application/json")
    public UserCalorieView create(@RequestBody @Valid final CreateUserCalorieRequest createUserCalorieRequest) {

        final Long userId = fetchUserIdFromAuth();
        Calorie calorie = calorieMapper.toCalorie(createUserCalorieRequest);
        calorie.setUserId(userId);
        return calorieMapper.toUserCalorieView(calorieService.createCalorie(calorie));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") final Long calorieId) {

        final Long userId = fetchUserIdFromAuth();
        calorieService.deleteById(userId, calorieId);
    }

    @PatchMapping(path = "/{id}")
    public UserCalorieView update(@PathVariable("id") final Long calorieId, @RequestBody @Valid final UpdateCalorieRequest updateCalorieRequest) {

        final Long userId = fetchUserIdFromAuth();
        Calorie originalCalorie = calorieService.findOneByUser(userId, calorieId);
        Calorie updatedCalorie = calorieMapper.updateCalorie(updateCalorieRequest, originalCalorie);

        return calorieMapper.toUserCalorieView(calorieService.updateById(userId, calorieId, updatedCalorie));
    }

    @PutMapping(path = "/{id}")
    public UserCalorieView replace(@PathVariable("id") final Long calorieId, @RequestBody @Valid final CreateUserCalorieRequest createUserCalorieRequest) {

        final Long userId = fetchUserIdFromAuth();
        Calorie updatedCalorie = calorieMapper.toCalorie(createUserCalorieRequest);
        updatedCalorie.setUserId(userId);


        return calorieMapper.toUserCalorieView(calorieService.replaceById(userId, calorieId, updatedCalorie));
    }
    private Long fetchUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((User) auth.getPrincipal()).getId();

    }

}
