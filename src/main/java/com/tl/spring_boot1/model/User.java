package com.tl.spring_boot1.model;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String name;
    private int age;

    // getter & setters

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}
