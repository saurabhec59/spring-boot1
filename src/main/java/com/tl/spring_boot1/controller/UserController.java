package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.dto.users.UserPaginatedResponseDTO;
import com.tl.spring_boot1.dto.users.UserPatchRequest;
import com.tl.spring_boot1.service.UserService;
import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.dto.users.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request){
         UserResponse response =  userService.createUser(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
        Whatever authentication the filter sets for a request as 'SecurityContextHolder.getContext().setAuthentication(authentication)'
        that authentication object remains available for that request for entire life cycle of that request.
        And @PreAuthorize checks that 'authentication' object.
        Remember in our jwt filter while creating object of 'authentication' we passed 3 args:
             UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,        // ← principal (the first parameter)
                    null,        // ← CREDENTIALS (password - we don't store it)
                    Collections.singletonList(authority) // ← AUTHORITIES (roles)
                );

         The first arg is called " PRINCIPLE" and that's why below we are able to check => "authentication.principle.id"
         #id == > it refers to the arg variable 'id' in the below method
     */

    // either 'ADMIN' or self
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable int id){
        UserResponse response = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // only ADMIN access (set in config class directly)
    @GetMapping("/users")
    public ResponseEntity<UserPaginatedResponseDTO> findAllUsers(
            @RequestParam( defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam( defaultValue = "asc") String order,
            @RequestParam( required = false) @Size( max = 50) String name,
            @RequestParam( required = false) @Size( max = 50) String email,
            @RequestParam( required = false) @Size( max = 50) String city,
            @RequestParam(required = false) @Min(0) Integer minAge,
            @RequestParam(required = false) @Min(0) Integer maxAge
    ){
        UserPaginatedResponseDTO response = userService.findAllUsers( page, limit, sort, order, name, email, city, minAge, maxAge);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // only 'ADMIN' or self
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable int id, @Valid @RequestBody UserPatchRequest request){
        UserResponse response = userService.patchUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // only 'ADMIN' or self
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
