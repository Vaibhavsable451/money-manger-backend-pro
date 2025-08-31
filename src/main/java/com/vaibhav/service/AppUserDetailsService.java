package com.vaibhav.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vaibhav.entity.ProfileEntity;
import com.vaibhav.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
	private final ProfileRepository profileRepository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	   ProfileEntity extistingProfile = profileRepository.findByEmail(email)
	   .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email"+ email));
	   
		return User.builder()
				.username(extistingProfile.getEmail())
				.password(extistingProfile.getPassword())
				.authorities(Collections.emptyList())
				.build();
	}

}
