package com.example.coopsysapp;

public class Einkauf {

	private int id, einkaueferId;
	private String name, datum;
	
	
	public Einkauf(int id, int einkaueferId, String name, String datum) {
		super();
		this.id = id;
		this.einkaueferId = einkaueferId;
		this.name = name;
		this.datum = datum;
	}


	public int getId() {
		return id;
	}


	public int getEinkauefer() {
		return einkaueferId;
	}


	public String getName() {
		return name;
	}


	public String getDatum() {
		return datum;
	}
	

	
	
}
