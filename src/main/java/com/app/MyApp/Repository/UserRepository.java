package com.app.MyApp.Repository;

import com.app.MyApp.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByemail(String email);
    Optional<User> findByusernameIgnoreCase(String user);
}
