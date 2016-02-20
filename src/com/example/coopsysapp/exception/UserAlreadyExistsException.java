package com.example.coopsysapp.exception;

public class UserAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5727832903324807669L;
	private String name;
	
	public UserAlreadyExistsException(String name) {
		this.name=name;
	}
	
	public String getMessage() {
		return("The user " + name + " already exists.");
	}
}
