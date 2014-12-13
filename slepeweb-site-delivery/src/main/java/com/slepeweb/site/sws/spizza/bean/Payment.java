package com.slepeweb.site.sws.spizza.bean;

import java.io.Serializable;

public class Payment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private CardType cardType;
	private String cardNumber, cardOwner, ccvCode;
	private String expiryDate;
	private boolean accepted;
	
	public enum CardType {
		Visa("Visa"), MasterCard("Mastercard"), Amex("American Express");
		
		private final String label;
		
		public String getKey() {
			return this.name();
		}
		
		public String getLabel() {
			return this.label;
		}
		
		CardType(String s) {
			this.label = s;
		}
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardOwner() {
		return cardOwner;
	}

	public void setCardOwner(String cardOwner) {
		this.cardOwner = cardOwner;
	}

	public String getCcvCode() {
		return ccvCode;
	}

	public void setCcvCode(String ccvCode) {
		this.ccvCode = ccvCode;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
}
