package com.tl.spring_boot1.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public class UserPatchRequest {
    // These are the only fields server allows client to modify or update
    // not doing mandatory check like 'notNull' .. because Patch allows any no of fields to be updated.

    private String name;

    @Min(0) // considers null value to be valid
    private Integer age;
    @Email // considers null value to be valid
    private String email;
    private String city;

    // getter & setters
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public String getEmail() { return email; }
    public String getCity() { return city; }

    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public void setEmail(String email) { this.email = email; }
    public void setCity(String city) { this.city = city; }
}
