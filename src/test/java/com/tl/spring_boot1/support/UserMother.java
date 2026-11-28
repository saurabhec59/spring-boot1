package com.tl.spring_boot1.support;

import com.tl.spring_boot1.model.User;

public class UserMother {
    public static User aUser(){
        User user = new User();
        user.setId(1);
        user.setName("TestUser1");
        user.setAge(20);
        user.setEmail("user1@test.com");
        user.setCity("Rome");
        user.setPassword("testuserpass1");
        user.setRole("USER");
        return user;
    }
}
