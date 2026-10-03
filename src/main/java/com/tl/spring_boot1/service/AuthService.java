package com.tl.spring_boot1.service;

import com.tl.spring_boot1.dto.auth.LoginRequestDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseDTO;
import com.tl.spring_boot1.dto.users.UserResponse;
import com.tl.spring_boot1.exception.InvalidUserDataException;
import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request){

        // check if user with email exists or not
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user == null){
            throw new InvalidUserDataException("Invalid email or password");// not authenticated client should not know whether this username is present or not.
        }
        //
        if( !passwordEncoder.matches( request.getPassword(), user.getPassword()) ){
            throw new InvalidUserDataException("Invalid email or password");
        }

        // generate jwt
        final String token = jwtUtil.generateJwtToken(user);

        // LoginResponseDTO will send a body with token, it's type, expiry time as 1 hour because same is used while token generation, and
        // user details (UserResponse dto, not sending 'user' object directly)
        return LoginResponseDTO.from(token, "Bearer", jwtUtil.getExpirationTimeInSeconds(), UserResponse.from(user) );
    }
}
