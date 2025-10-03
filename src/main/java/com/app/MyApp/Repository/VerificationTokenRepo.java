package com.app.MyApp.Repository;

import com.app.MyApp.Model.User;
import com.app.MyApp.Model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenRepo extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}
