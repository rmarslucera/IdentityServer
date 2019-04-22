package com.lucera.identity.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.lucera.identity.auth.security.jwt.JwtProvider;
import com.lucera.identity.auth.security.services.UserPrinciple;

import io.jsonwebtoken.Claims;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtAuthTests {
	
	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
	JwtProvider provider;
	
	
	@Test
    public void testCreateToken() {
			
		String token = provider.generateJwtToken(Constants.getAuth());
		
		assertThat(token).isNotNull();

    }
	
	@Test
    public void validateToken() {
		String token = provider.generateJwtToken(Constants.getAuth());
		
		assertThat(token).isNotNull();
		
		assertThat( provider.validateJwtToken(token) ).isTrue() ;
	
	}
	
	@Test
    public void validateClaims() {
		String token = provider.generateJwtToken(Constants.getAuth());
		
		assertThat(token).isNotNull();

		Claims claims = provider.getClaimsFromJwtToken(token);
		
		assertThat(claims).isNotNull();
		assertThat(claims.get("clientProfile")).isNotNull();
		
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> clientProfile = gson.fromJson((String) claims.get("clientProfile"), Map.class);
		
		assertThat(claims.get("clientProfile")).isNotNull();
		assertThat(clientProfile).isNotNull();
		assertThat(clientProfile.get("domain")).isNotNull();		
	
	}
	
	@Test
    public void validateUserName() {
		String token = provider.generateJwtToken(Constants.getAuth());
		
		assertThat(token).isNotNull();

		String userName = provider.getUserNameFromJwtToken(token);
		
		assertThat(userName).isNotNull();
		assertThat(userName).isEqualTo(Constants.getUser().getUsername());
		
	
	}


}
