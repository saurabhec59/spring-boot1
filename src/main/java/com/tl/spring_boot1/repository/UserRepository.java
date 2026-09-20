package com.tl.spring_boot1.repository;

import com.tl.spring_boot1.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public User createUser(User user){
        em.persist(user); // when em.persist() executes successfully then it modifies (updates with id) the same object which was passed so that same reference variable can be returned.
        return user;
    }

    public User findUserById(int id){
        return em.find(User.class, id);
    }

    public List<User> findAllUsers(){
        return em.createQuery("SELECT u FROM User u", User.class).getResultList(); // This will return a List<User>
    }

}
