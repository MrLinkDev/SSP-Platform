package com.ssp.platform.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
	private boolean success = true;
	private String token;
	private String type = "Bearer";
	/*
	private String username;
	private String email;
	private List<String> roles;
	*/
	public JwtResponse(String accessToken) {
		this.token = accessToken;
		/*
		this.username = username;
		this.email = email;
		this.roles = roles;
		 */
	}
}
