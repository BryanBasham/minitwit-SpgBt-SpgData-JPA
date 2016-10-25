package com.minitwit.web;

import com.minitwit.domain.User;

public class LoginResult {
	
	private String error;
	
	private User user;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean hasUser() {
		return user != null;
	}

}
