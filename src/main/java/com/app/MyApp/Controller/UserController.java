package com.app.MyApp.Controller;

import com.app.MyApp.Model.LoginRequest;
import com.app.MyApp.Model.LoginResponse;
import com.app.MyApp.Model.PasswordResetBody;
import com.app.MyApp.Model.User;
import com.app.MyApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity createUser(@RequestBody User user)
    {
        String email= user.getEmail();
        if(userService.getUserByEmail(email) != null){
            System.out.println("User Exist");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        userService.addUser(user);
        System.out.println("User Added Successfully");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity verifyEmail(@RequestParam String token)
    {
        System.out.println("In verifyEmail?token=" + token);
        if(userService.verifyToken(token))
            return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> dologin(@RequestBody LoginRequest loginRequest){
        System.out.println("In login");
        String jwt = null;
        jwt = userService.login(loginRequest.getUsername(),loginRequest.getPassword());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(jwt);
        System.out.println(jwt);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUserProfile(@AuthenticationPrincipal User user) {
        System.out.println("In UserRestController(getLoggedInUserProfile)");
        System.out.println("USER-EMAIL : " + user.getEmail());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestBody Map<String ,String > mail){
        userService.forgotPassword(mail.get("email"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset")
    public ResponseEntity resetPassword(@RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }
}
