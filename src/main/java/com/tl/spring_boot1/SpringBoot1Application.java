package com.tl.spring_boot1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBoot1Application {
    // starting

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBoot1Application.class, args);// #1...
	}

}

/*
#1.....
This line starts the Spring Boot application and returns object of Spring Ioc container. Spring Ioc container is responsible dependency injection means it manages the life cycle( create, manage, destroy)
of Spring Beans. Spring Beans are the objects managed by IOC container.
 */