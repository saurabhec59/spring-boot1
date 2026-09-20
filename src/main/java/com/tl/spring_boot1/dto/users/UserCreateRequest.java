package com.tl.spring_boot1.dto.users;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserCreateRequest {
    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer age;

    // Getter & Setters
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
