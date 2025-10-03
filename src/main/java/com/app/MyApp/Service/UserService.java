package com.app.MyApp.Service;

import com.app.MyApp.Model.PasswordResetBody;
import com.app.MyApp.Model.User;
import com.app.MyApp.Model.VerificationToken;
import com.app.MyApp.Repository.UserRepository;
import com.app.MyApp.Repository.VerificationTokenRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepo verificationTokenRepo;

    public User getUserByEmail(String email){
        Optional<User> op = userRepository.findByemail(email);
        if(op.isPresent())
            return op.get();
        return null;
    }

    @Transactional
    public User addUser(User user){
        user.setPassword(encryptionService.encryptPassword(user.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        System.out.println("Mail Sent");
        User u=userRepository.save(user);
        verificationTokenRepo.save(verificationToken);
        return u;
    }

    private VerificationToken createVerificationToken(User user){
        System.out.println("In createVerificationToken");
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateJWTForVerification(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String login(String userName, String password){
        Optional<User> op = userRepository.findByusernameIgnoreCase(userName);
        if(op.isPresent()){
            User user = op.get();
            if(encryptionService.verifyPassword(password,user.getPassword())){
                if(user.isEmailVerified()) {
                    System.out.println("Generating JWT");
                    return jwtService.generateJWT(user);
                }else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = (verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - 60*60*1000)));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenRepo.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                }
            }
        }else {
            System.out.println("Kat Gya");
        }
        return null;
    }

    @Transactional
    public Boolean verifyToken(String token){
        System.out.println("In verifyToken");
        Optional<VerificationToken> op =verificationTokenRepo.findByToken(token);
        if(op.isPresent()){
            User user = op.get().getUser();
            System.out.println("token is verified for user : " + user.getUsername());
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                userRepository.save(user);
                return true;
            }
        }
        else {
            System.out.println("Kat Gya");
        }
        return false;
    }

    public void forgotPassword(String mail){
        Optional<User> op = userRepository.findByemail(mail);
        System.out.println(mail);
        if(op.isPresent()){
            User user = op.get();
            String token = jwtService.generateJWTForPasswordReset(user);
            emailService.sendPasswordResetMail(user,token);
        }else {
            System.out.println("Kat Gya");
        }
    }

    public void resetPassword(PasswordResetBody body){
        System.out.println("In resetPassword");
        Optional<VerificationToken> op = verificationTokenRepo.findByToken(body.getToken());
        if(op.isPresent()){
            User user = op.get().getUser();
            user.setPassword(encryptionService.encryptPassword(user.getPassword()));
            userRepository.save(user);
        } else{
            System.out.println("Kat Gya");
        }
    }
}
