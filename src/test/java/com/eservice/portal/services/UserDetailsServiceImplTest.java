package com.eservice.portal.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.eservice.portal.model.ERole;
import com.eservice.portal.model.Role;
import com.eservice.portal.model.User;
import com.eservice.portal.repository.UserRepository;
import com.eservice.portal.service.UserDetailsImpl;
import com.eservice.portal.service.UserDetailsServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Test
	public void testLoadUserByUsername() {
		Role role = new Role();
		role.setId(1);
		role.setName(ERole.ROLE_CUSTOMER);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		User user = new User();
		user.setId(1l);
		user.setEmail("customer@eservice.com");
		user.setPassword("pass@123");
		user.setUsername("customer");
		user.setRoles(roles);
		
		when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
		UserDetails userDetailsImpl = userDetailsServiceImpl.loadUserByUsername(user.getUsername());
		Assertions.assertEquals(userDetailsImpl.getUsername(), user.getUsername());
	}
	
	
}
