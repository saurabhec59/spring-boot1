package com.tl.spring_boot1.dto.users;

import jakarta.validation.constraints.*;

public class UserCreateRequest {
    /*
        This DTO is preventing field 'id' not to be set by client because that is auto generated.
        Also, this DTO is preventing field 'role' not to be set by user itself.
        So even though client send 'id' & 'role', this DTO will discard them.
     */

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer age;

    @NotNull
    @Email
    private String email;
    private String city;

    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    // Getter & Setters
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public String getEmail() {
        return email;
    }
    public String getCity() {
        return city;
    }
    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public void setEmail(String email) { this.email = email; }
    public void setCity(String city) { this.city = city; }
    public void setPassword(String password) { this.password = password; }
}
