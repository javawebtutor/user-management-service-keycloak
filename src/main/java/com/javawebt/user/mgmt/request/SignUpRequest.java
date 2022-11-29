package com.javawebt.user.mgmt.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
	
	private String firstName;
	private String lastName;
	private String emailId;
	private String mobileNumber;
	private String password;
	private String confirmPassword;

}
