package com.example.coopsysapp;

public class Debt {
	private int schuldner, glaubiger;
	private float betrag;
	
	public Debt(int schuldner, int glaubiger, float betrag) {
		super();
		this.schuldner = schuldner;
		this.glaubiger = glaubiger;
		this.betrag = betrag;
	}

	public int getSchuldner() {
		return schuldner;
	}

	public int getGlaubiger() {
		return glaubiger;
	}

	public float getBetrag() {
		return betrag;
	}
	
	
}
