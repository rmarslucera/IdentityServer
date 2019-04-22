package com.lucera.identity.auth;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import com.lucera.identity.auth.model.User;
import com.lucera.identity.auth.repository.UserRepository;

import static org.assertj.core.api.Assertions.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepoTests {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository repository;

	private String NAME = "Joe Blow";
	private String USER_NAME="jb1234";
	private String EMAIL="jb1234@email.com";
	private String PASSWORD = "abc123";
	
	@Test
	public void testCreateUser() throws Exception {
		
		createUser();
				
		Optional<User> res = this.repository.findByUsername(USER_NAME);
		User user = res.get();
		
		assertThat(user.getUsername()).isEqualTo(USER_NAME);
		assertThat(user.getName()).isEqualTo(NAME);
		assertThat(user.getEmail()).isEqualTo(EMAIL);
		assertThat(user.getPassword()).isEqualTo(PASSWORD);
	}
	
	@Test
	public void testExistsByEmail() {
		createUser();
		assertThat(repository.existsByEmail(EMAIL)).isTrue();
	}

	@Test
	public void testExistsByUsername() {
		createUser();
		assertThat(repository.existsByUsername(USER_NAME)).isTrue();
	}
	
	private void createUser() {
				
		this.entityManager.persist(Constants.getUser());
	}
}
