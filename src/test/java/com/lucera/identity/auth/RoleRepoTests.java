package com.lucera.identity.auth;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import com.lucera.identity.auth.model.Role;
import com.lucera.identity.auth.model.RoleName;
import com.lucera.identity.auth.repository.RoleRepository;

import static org.assertj.core.api.Assertions.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepoTests {
	
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private RoleRepository repository;

	@Test
	public void testUserRepo() throws Exception {
		
		
		this.entityManager.persist(new Role(RoleName.ROLE_BROKER));
		
		Optional<Role> res = this.repository.findByName(RoleName.ROLE_BROKER);
		Role role = res.get();
		
		assertThat(role.getName()).isEqualTo(RoleName.ROLE_BROKER);
		
	}

}
