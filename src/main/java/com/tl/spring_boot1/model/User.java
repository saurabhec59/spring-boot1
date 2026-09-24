/*
    Entity should describe only DB STRUCTURE like @Column(....) not the validations like @Valid
    Validation logic is responsibility of DTO layer.
 */

package com.tl.spring_boot1.model;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column( nullable = false )
    private String name;
    private Integer age;
    @Column( unique = true,  nullable = false) // this is just a check & if the db column already exist without unique constraint then this will do nothing
    private String email;
    private String city;
    private String role;

    // getter & setters

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public String getRole() { return role; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(Integer age) { this.age = age; }
    public void setEmail(String email) { this.email = email; }
    public void setCity(String city) { this.city = city; }
    public void setRole(String role) { this.role = role; }
}
