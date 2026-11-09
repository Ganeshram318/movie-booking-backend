package com.movie.backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestDto {
	
	private String firstName;

	private String lastName;
	
	private String email;
	
	private String phone;
	
	private String password;
	
	private String confirmPassword;

}
