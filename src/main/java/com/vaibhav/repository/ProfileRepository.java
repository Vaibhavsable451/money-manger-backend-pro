package com.vaibhav.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhav.entity.ProfileEntity;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long>{

	//select * from tbl_profiles where email=?
	Optional<ProfileEntity> findByEmail(String email);
	
	Optional<ProfileEntity> findByActivationToken(String activationToken);
}
