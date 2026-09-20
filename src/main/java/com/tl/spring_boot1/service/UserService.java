package com.tl.spring_boot1.service;

import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.dto.users.UserCreateRequest;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.dto.users.UserResponse;
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
}
