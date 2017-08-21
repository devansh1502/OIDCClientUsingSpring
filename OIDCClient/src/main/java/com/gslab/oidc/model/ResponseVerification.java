package com.gslab.oidc.model;

public class ResponseVerification {
	private long iat, exp;
	private String iss, aud, nonce_resp;

	public long getIat() {
		return iat;
	}

	public void setIat(long iat) {
		this.iat = iat;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getNonce_resp() {
		return nonce_resp;
	}

	public void setNonce_resp(String nonce_resp) {
		this.nonce_resp = nonce_resp;
	}

}
