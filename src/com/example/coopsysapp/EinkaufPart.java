package com.example.coopsysapp;

public class EinkaufPart {
	
	private int einkaufId, gastId;
	private float betrag;
	private String notiz;
	
	public EinkaufPart(int einkaufId, int gastId, float betrag, String notiz) {
		super();
		this.einkaufId = einkaufId;
		this.gastId = gastId;
		this.betrag = betrag;
		this.notiz = notiz;
	}

	public int getEinkaufId() {
		return einkaufId;
	}

	public int getGastId() {
		return gastId;
	}

	public float getBetrag() {
		return betrag;
	}

	public String getNotiz() {
		return notiz;
	}
}
