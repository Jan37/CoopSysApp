package com.example.coopsysapp.exception;

public class FunctionNotDefinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2032469709309615772L;
	private final String functionCall;
	
	public FunctionNotDefinedException(String functionCall){
		this.functionCall = functionCall;
	}
	
	public String getMessage() {
		return("Function " + functionCall + " does not exist.");
	}
	
}
