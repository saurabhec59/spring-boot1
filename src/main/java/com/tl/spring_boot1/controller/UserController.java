package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.service.UserService;
import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.dto.users.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request){
         UserResponse response =  userService.createUser(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
