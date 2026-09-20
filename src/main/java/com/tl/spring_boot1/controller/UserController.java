package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.dto.users.UserPatchRequest;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.service.UserService;
import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.dto.users.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request){
         UserResponse response =  userService.createUser(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable int id){
        UserResponse response = userService.findUserById(id);
        if(response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> findAllUsers(){
        List<UserResponse> response = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable int id, @Valid @RequestBody UserPatchRequest request){
        UserResponse response = userService.patchUser(id, request);
        if(response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id){
        boolean response = userService.deleteUser(id);
        if(!response){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
