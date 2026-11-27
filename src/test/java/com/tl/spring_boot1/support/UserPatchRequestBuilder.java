package com.tl.spring_boot1.support;

import com.tl.spring_boot1.dto.users.UserPatchRequest;

public class UserPatchRequestBuilder {
    private String name = "testUser patched";
    private Integer age = 30;
    private String email = "userpatched@test.com";
    private String city = "test city patched";

    public static UserPatchRequestBuilder aUserPatchRequestBuilder(){
        UserPatchRequestBuilder userPatchRequestBuilder = new UserPatchRequestBuilder();
        return userPatchRequestBuilder;
    }

    public UserPatchRequestBuilder withName(String name){
        this.name = name;
        return this;
    }

    public UserPatchRequestBuilder withAge(Integer age){
        this.age = age;
        return this;
    }

    public UserPatchRequestBuilder withEmail(String email){
        this.email = email;
        return this;
    }

    public UserPatchRequestBuilder withCity(String city){
        this.city = city;
        return this;
    }

    public UserPatchRequest build(){
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setName(name);
        userPatchRequest.setAge(age);
        userPatchRequest.setEmail(email);
        userPatchRequest.setCity(city);
        return userPatchRequest;
    }

}
