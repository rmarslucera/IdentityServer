package com.lucera.identity.auth.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucera.identity.auth.message.request.LoginForm;
import com.lucera.identity.auth.message.request.SignUpForm;
import com.lucera.identity.auth.model.Role;
import com.lucera.identity.auth.model.RoleName;
import com.lucera.identity.auth.model.User;
import com.lucera.identity.auth.repository.RoleRepository;
import com.lucera.identity.auth.repository.UserRepository;
import com.lucera.identity.auth.security.jwt.JwtProvider;
import com.lucera.identity.exceptions.RoleNotFoundException;
import com.lucera.identity.message.response.JwtResponse;

@RestController
@RequestMapping("/lucera/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Error -> Username is already taken!",
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Error -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User( signUpRequest.getName(), 
        					  signUpRequest.getUsername(),
        					  signUpRequest.getEmail(), 
        					  encoder.encode(signUpRequest.getPassword()) );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
        	switch(role) {
	    		case "admin":
	    			Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
	                .orElseThrow(() -> new RoleNotFoundException("Error! -> Cause: Admin Role not found."));
	    			roles.add(adminRole);
	    			
	    			break;
	    		case "broker":
	            	Role brokerRole = roleRepository.findByName(RoleName.ROLE_BROKER)
	                .orElseThrow(() -> new RoleNotFoundException("Error! -> Cause: Broker Role not found."));
	            	roles.add(brokerRole);
	            	
	    			break;
	    		case "forex":
	            	Role forex = roleRepository.findByName(RoleName.ROLE_FOREX)
	                .orElseThrow(() -> new RoleNotFoundException("Error! -> Cause: Forex Role not found."));
	            	roles.add(forex);
	            	
	    			break;
	    		default:
	        		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
	                .orElseThrow(() -> new RoleNotFoundException("Error! -> Cause: User Role not found."));
	        		roles.add(userRole);        			
        	}
        });
        
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok().body("User registered successfully!");
    }
}