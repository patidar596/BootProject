package com.app.MyApp.Security;

import com.app.MyApp.Model.User;
import com.app.MyApp.Repository.UserRepository;
import com.app.MyApp.Service.JWTService;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    public JWTRequestFilter() {
        System.out.println("In JWTRequestFilter");
    }

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("In JWTRequestFilter(doFilterInternal)");
        String token = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken authToken = checkToken(token);
        if(authToken != null){
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        filterChain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken checkToken(String token){
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
            try {
                String username = jwtService.getUsername(token);
                System.out.println("username : " + username);
                Optional<User> opUser = userRepo.findByusernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    User user = opUser.get();
                    /*
                        used in extracting logged-in user
                        from security context holder
                     */
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return authentication;
                }
            } catch (JWTDecodeException ex) {
                System.out.println("In Catch(checkToken)");
            }
        }else {
            System.out.println("No Token");
        }
        System.out.println("Authentication set null");
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }
}
