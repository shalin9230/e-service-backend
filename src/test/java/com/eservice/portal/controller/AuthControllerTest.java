package com.eservice.portal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.eservice.portal.configuration.security.jwt.JwtUtils;
import com.eservice.portal.model.ERole;
import com.eservice.portal.model.Role;
import com.eservice.portal.model.User;
import com.eservice.portal.payload.request.LoginRequest;
import com.eservice.portal.payload.request.SignupRequest;
import com.eservice.portal.payload.response.JwtResponse;
import com.eservice.portal.service.UserDetailsImpl;
import com.eservice.portal.service.UserSignupService;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

	@Mock
	AuthenticationManager authenticationManager;
	
	@Mock
	Authentication authentication;
	
	@Mock
	JwtUtils jwtUtils;
	
	@Mock
	UserSignupService userSignupService;
	
	@InjectMocks
	private AuthController authControllerTest;
	
	@Test
	public void testAuthenticateUser() {
		LoginRequest loginRequest = getLoginRequest();
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(getAuthentication(loginRequest));
		when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("$2a$10$eKlLTh8f4h.7uKQL51Qx1eCK9AHiOpSLbaURM2Qr5kyv.eRg6Y4/S");
		ResponseEntity<?> response = authControllerTest.authenticateUser(getLoginRequest());
		JwtResponse jwtResponse = (JwtResponse) response.getBody();
		Assertions.assertEquals(response.getStatusCodeValue(), 200);
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
		Assertions.assertEquals(jwtResponse.getUsername(), loginRequest.getUsername());
	}
	
	@Test
	public void testRegisterUser() {
		when(userSignupService.signupService(any())).thenReturn("User registered successfully!");
		ResponseEntity<?> response = authControllerTest.registerUser(getSignupRequest());
		Assertions.assertEquals(response.getStatusCodeValue(), 200);
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	public void testAuthenticationFailed() {
		LoginRequest loginRequest = getLoginRequest();
		loginRequest.setUsername("customer5");
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(getAuthentication(loginRequest));
		when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn(null);
		ResponseEntity<?> response = authControllerTest.authenticateUser(getLoginRequest());
		JwtResponse jwtResponse = (JwtResponse) response.getBody();
		Assertions.assertEquals(jwtResponse.getAccessToken(), null);
	}
	private LoginRequest getLoginRequest() {
		LoginRequest loginRequest  = new LoginRequest();
		loginRequest.setUsername("customer");
		loginRequest.setPassword("pass@123");
		
		return loginRequest;
	}
	
	private UserDetailsImpl getUserDetails() {
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
		
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(rolel -> new SimpleGrantedAuthority(rolel.getName().name()))
				.collect(Collectors.toList());
		
		return new UserDetailsImpl(
				user.getId(), 
				user.getUsername(), 
				user.getEmail(),
				user.getPassword(), 
				authorities);
	}
	
	private Authentication getAuthentication(LoginRequest loginRequest) {
		return new UsernamePasswordAuthenticationToken(getUserDetails(), loginRequest.getPassword());
	}
	
	private SignupRequest getSignupRequest() {
		Set<String> role = new HashSet<>();
		role.add("ROLE_CUSTOMER");
		
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("customer@eservice.com");
		signupRequest.setPassword("pass@123");
		signupRequest.setUsername("customer");
		signupRequest.setRole(role);
		
		return signupRequest;
	}
}
