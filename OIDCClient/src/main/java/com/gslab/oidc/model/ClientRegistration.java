package com.gslab.oidc.model;
 /**
  * 
  * @author GS-1547
  * Class for declaring key paramaters required for OIDC Client Configuration 
  */
public class ClientRegistration {
	private String authorizationTokenEndpoint;
	private String tokenEndpoint;
	private String tokenKeysEndpoint;
	private String clientId;
	private String clientSecret;
	private String scope;
	private String authorizationCodeFlow;
	
	private String payload;
	public String getAuthorizationTokenEndpoint() {
		return authorizationTokenEndpoint;
	}
	public void setAuthorizationTokenEndpoint(String authorizationTokenEndpoint) {
		this.authorizationTokenEndpoint = authorizationTokenEndpoint;
	}
	public String getTokenEndpoint() {
		return tokenEndpoint;
	}
	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}
	public String getTokenKeysEndpoint() {
		return tokenKeysEndpoint;
	}
	public void setTokenKeysEndpoint(String tokenKeysEndpoint) {
		this.tokenKeysEndpoint = tokenKeysEndpoint;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getAuthorizationCodeFlow() {
		return authorizationCodeFlow;
	}
	public void setAuthorizationCodeFlow(String authorizationCodeFlow) {
		this.authorizationCodeFlow = authorizationCodeFlow;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
		
}