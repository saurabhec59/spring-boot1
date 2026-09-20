package com.tl.spring_boot1.service;

import com.tl.spring_boot1.dto.users.UserPatchRequest;
import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.dto.users.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse createUser( UserCreateRequest request){
        User user = new User();
        user.setName( request.getName() );
        user.setAge( request.getAge() );
        User newUser = userRepository.createUser(user);

        // sending this newly created user object to DTO
        return UserResponse.from(newUser);
    }

    public UserResponse findUserById(int id){
        User user =  userRepository.findUserById(id);
        if(user == null){
            return null;
        }
        UserResponse response = UserResponse.from(user);
        return response;
    }

    public List<UserResponse> findAllUsers(){
        List<User> allUsers = userRepository.findAllUsers();
        /*
            No need to check null because em.createQuery().getResultList() returns an empty List even in case of if there are nothing to return
            and does not return null.
        */
        List<UserResponse> response = new ArrayList<UserResponse>();
        for(User user : allUsers){
            response.add( UserResponse.from(user) );
        }
        return response;
    }

    @Transactional
    public UserResponse patchUser(int id, UserPatchRequest request){
        User user = userRepository.findUserById(id);
        if(user == null){
            return null;
        }
        // Now lets update existing returned user
        // Get all the fields from request
        String name = request.getName();
        Integer age = request.getAge(); // 'Integer' so that if not send by client so we can find out by null check

        // check if clint send 'name' as "" or "  "
        // because while creating 'user' the server is not allowing 'NULL' as well as these empty ("") & blank (" ") values. So update should follow that as well.
        if(name.isBlank()){ // .isBlank() returns true for "" & " " as well
            return null;
        }

        // Update Existing user
        if(name != null){
            user.setName(name);
        }
        if(age != null){
            user.setAge(age);
        }

        // DTO for this update 'user' and return UserResponse
        return UserResponse.from(user);

        /*
            @Transactional on the service method means this method runs inside a single JPA transaction.
            Rhe returned User object 'user' is a managed entity for the duration of this transaction.
            Any setter call on a managed entity is tracked by Hibernate ("dirty checking"). When the transaction commits (i.e., when patchUser() returns successfully),
            Hibernate automatically compares the entity's current state to what it originally loaded and issues an UPDATE SQL only for the changed columns.
         */
    }
}
