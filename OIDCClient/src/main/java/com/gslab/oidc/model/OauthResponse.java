package com.gslab.oidc.model;
/**
 * 
 * @author GS-1547
 * class for response type (id Token or exchange code)
 */
public class OauthResponse {

	private String idToken;
	private String exchangeCode;
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	public String getExchangeCode() {
		return exchangeCode;
	}
	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	
}
