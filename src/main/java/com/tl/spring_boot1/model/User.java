package com.tl.spring_boot1.model;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;

    // getter & setters

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(Integer age) { this.age = age; }
}
