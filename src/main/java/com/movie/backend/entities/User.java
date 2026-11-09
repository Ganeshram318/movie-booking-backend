package com.movie.backend.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.movie.backend.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
private String id;
	@Column(nullable = false,length = 100)
	private String firstName;
	@Column(nullable = false,length = 100)
	private String lastName;
	@Column(nullable = false,unique = true,  length= 100)
	private String email;
	@Column(nullable = false,length = 100)
	private String phone;
	@Column(nullable = false,length = 255)
	private String password;
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		 return List.of(
			        new SimpleGrantedAuthority(role.name())
			    );
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
	
	@Override
	public String getPassword() {
	    return password;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role = Role.ROLE_USER;

}
