package dub.spring.gutenberg.api;

import java.io.Serializable;

import dub.spring.gutenberg.service.Selectable;

public class PaymentMethod implements Selectable, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String cardNumber;
	private String name;
	
	public PaymentMethod() {}
	
	public PaymentMethod(String cardNumber, String name) {
		this.cardNumber = cardNumber;
		this.name = name;
	}
	
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
