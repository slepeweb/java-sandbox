package com.slepeweb.sandbox.spizza.bean;

import java.io.Serializable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.slepeweb.sandbox.spizza.bean.Payment.CardType;

public class PaymentForm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String cardType, cardNumber, cardOwner, expiryDate, ccvCode;

	public CardType[] getCardOptions() {
		return CardType.values();
	}
	
	@NotEmpty
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Digits(integer=16, fraction=0)
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@NotEmpty
	public String getCardOwner() {
		return cardOwner;
	}

	public void setCardOwner(String cardOwner) {
		this.cardOwner = cardOwner;
	}

	@Pattern(regexp="^\\d\\d/\\d\\d\\d\\d$")
	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Digits(integer=3, fraction=0)
	public String getCcvCode() {
		return ccvCode;
	}

	public void setCcvCode(String ccvCode) {
		this.ccvCode = ccvCode;
	}
}
