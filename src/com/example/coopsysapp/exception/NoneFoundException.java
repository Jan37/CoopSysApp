package com.example.coopsysapp.exception;

public class NoneFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -904883166204284087L;

	public String getMessage() {
		return("The List is empty.");
	}
}
