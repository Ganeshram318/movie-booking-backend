package com.movie.backend.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.movie.backend.Service.EmailService;
@Service
public class EmailserviceImpl implements EmailService{

	 @Autowired
	    private JavaMailSender mailSender;

	 @Override
	 public void sendRegistrationEmail(String to, String name) {
		 SimpleMailMessage message = new SimpleMailMessage();

		 message.setFrom("gr312003@gmail.com");
	        message.setTo(to);
	        message.setSubject("Registration Successful");
	        message.setText(
	            "Hello " + name + ",\n\n" +
	            "Your registration was successful.\n\n" +
	            "Thank you for registering!"
	        );

	        mailSender.send(message);
}
		
	 }
	 
	 

	 
