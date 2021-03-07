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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eservice.portal.model.ERole;
import com.eservice.portal.model.Role;
import com.eservice.portal.model.User;
import com.eservice.portal.payload.request.SignupRequest;
import com.eservice.portal.repository.RoleRepository;
import com.eservice.portal.repository.UserRepository;
import com.eservice.portal.service.UserSignupService;
import com.eservice.portal.service.UserSignupServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserSignupServiceImplTest {

	@Mock
	UserRepository userRepository;
	
	@Mock
	RoleRepository roleRepository;
	
	@Mock
	PasswordEncoder encoder;
	
	@InjectMocks
	UserSignupServiceImpl userSignupService;
	
	@Test
	public void testSignupService() {
		Role role = new Role();
		role.setId(1);
		role.setName(ERole.ROLE_ADMIN);
		
		Set<Role> roled = new HashSet<>();
		roled.add(role);
		
		User user = new User();
		user.setId(1l);
		user.setEmail("customer@eservice.com");
		user.setPassword("pass@123");
		user.setUsername("customer");
		user.setRoles(roled);
		
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("admin1@eservice.com");
		signupRequest.setPassword("pass@123");
		signupRequest.setUsername("admin11");
		signupRequest.setRole(roles);
		
		when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
		when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
		when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		String response = userSignupService.signupService(signupRequest);
		Assertions.assertEquals(response, "User registered successfully!");
	}
	
	@Test
	public void testWithExistingUser() {
		
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("admin1@eservice.com");
		signupRequest.setPassword("pass@123");
		signupRequest.setUsername("admin11");
		signupRequest.setRole(roles);
		
		when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

		
		String response = userSignupService.signupService(signupRequest);
		Assertions.assertEquals(response, "Error: Username is already taken!");
	}
	
	@Test
	public void testWithExistingEmailId() {
		
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("admin1@eservice.com");
		signupRequest.setPassword("pass@123");
		signupRequest.setUsername("admin11");
		signupRequest.setRole(roles);
		
		when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
		when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
		
		String response = userSignupService.signupService(signupRequest);
		Assertions.assertEquals(response, "Error: Email is already in use!");
	}
	
	@Test
	public void testNoRole() {
		
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("admin1@eservice.com");
		signupRequest.setPassword("pass@123");
		signupRequest.setUsername("admin11");
		
		when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
		when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
		when(roleRepository.findByName(any())).thenThrow(new RuntimeException("Error: Role is not found."));
		
	}
}
