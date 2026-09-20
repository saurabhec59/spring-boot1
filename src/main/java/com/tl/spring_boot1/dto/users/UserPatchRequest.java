package com.tl.spring_boot1.dto.users;

import jakarta.validation.constraints.Min;

public class UserPatchRequest {
    // These are the only fields server allows client to modify or update
    // not doing mandatory check like 'notNull' .. because Patch allows any no of fields to be updated.

    private String name;

    @Min(0)
    private Integer age;

    // getter & setters
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
