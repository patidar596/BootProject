package com.app.MyApp.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    @Value("${encryption.salt.rounds}")
    private int saltRounds;

    private String salt;

    @PostConstruct
    public void postconstruct()
    {
        salt= BCrypt.gensalt(saltRounds);
    }

    public String encryptPassword(String password)
    {
        return BCrypt.hashpw(password,salt);
    }

    public boolean verifyPassword(String password,String hash)
    {
        return BCrypt.checkpw(password, hash);
    }
}
