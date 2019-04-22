package com.lucera.identity.auth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucera.identity.auth.message.request.LoginForm;
import com.lucera.identity.auth.message.request.SignUpForm;
import com.lucera.identity.auth.model.Role;
import com.lucera.identity.auth.model.RoleName;
import com.lucera.identity.auth.model.User;
import com.lucera.identity.auth.security.services.UserPrinciple;

public class Utils {
	
	private static String NAME = "Joe Blow";
	private static String USER_NAME="jb1234";
	private static String EMAIL="jb1234@email.com";
	private static String PASSWORD = "abc123";
	
	public static User getUser() {
		return new User(NAME, USER_NAME, EMAIL, PASSWORD);
	}
	
	public static LoginForm getLoginForm() {
		LoginForm form = new LoginForm();
		form.setUsername(USER_NAME);
		form.setPassword(PASSWORD);
		return form;
		
	}
	
	public static SignUpForm getSignUpForm(String role) {
		SignUpForm form = new SignUpForm();
		form.setName(NAME);
		form.setUsername(USER_NAME);
		form.setPassword(PASSWORD);
		form.setEmail(EMAIL);
		form.setPassword(PASSWORD);
		form.setRole( new HashSet<>( Arrays.asList( role )));
		return form;
	}

	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static Authentication getAuth() {
		Object credentials=null;
		UserPrinciple principal = new UserPrinciple(0L,"",USER_NAME,"","",null );
		
		return new TestingAuthenticationToken(principal, credentials);
	}
}
