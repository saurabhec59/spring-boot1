package com.tl.spring_boot1.support;

import com.tl.spring_boot1.dto.auth.LoginRequestDTO;

public class LoginRequestDTOBuilder {
    private String email = "testEmailDefault@test.com";
    private String password = "defaulttestpassword";

    public static LoginRequestDTOBuilder aLoginRequestDTO() {
        LoginRequestDTOBuilder loginRequestDTOBuilder = new LoginRequestDTOBuilder();
        return loginRequestDTOBuilder;
    }

    public LoginRequestDTOBuilder withEmail(String email){
        this.email = email;
        return this;
    }

    public LoginRequestDTOBuilder withPassword(String password){
        this.password = password;
        return this;
    }

    public LoginRequestDTO build(){
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);
        return loginRequestDTO;
    }
}
