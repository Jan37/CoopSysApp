package com.example.coopsysapp.exception;

public class NotFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -904883166204284087L;
	private String searchedItem;
	private boolean multiple;

	public NotFoundException(String searchedItem, boolean multiple) {
		this.searchedItem = searchedItem;
		this.multiple = multiple;
	}
	
	public String getMessage() {
		if (multiple) {
			return("No " + searchedItem + "s were found");
		}
		return("The "+ searchedItem + " was not found.");
	}
}
