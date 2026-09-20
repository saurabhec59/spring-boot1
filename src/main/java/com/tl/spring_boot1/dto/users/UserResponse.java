/*
    This is a DTO for sending User Response & this will control what fields of User will be returned to client.
*/

package com.tl.spring_boot1.dto.users;

import com.tl.spring_boot1.model.User;

public class UserResponse {

    /*
        Here we are converting db returned 'User' object into this UserResponse object and we are controlling what fields needs to be
        sent to client in response.
        Even if db returned hashed passwords and credentials then also this 'UserResponse' object will contain only 'id', 'name' & 'age'
     */

    private Integer id;
    private String name;
    private Integer age;

    UserResponse(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static UserResponse from(User user){
        return new UserResponse(user.getId(), user.getName(), user.getAge());
    }

    // getters because at the end this 'UserResponse' object will be sent so jackson need to convert the fields of this into json format
    // but it can't access private fields from object only.

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
}
