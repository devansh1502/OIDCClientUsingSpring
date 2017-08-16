package com.gslab.oidc.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
//import java.util.Enumeration;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;
import com.gslab.oidc.jsonViews.Views;
import com.gslab.oidc.model.ClientRegistration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
@Controller
public class HomeController {
	
	private String redirectUri = "https://localhost:8443/OIDCClient/startOAuth/_callback";
	
	private String authUrl = null;
	private String tokenUrl = null;
	private String exhangecode = null ;
	private String payload = null ;
	private String payloadIm = null;
	ClientRegistration cR = null ;
	
	String sessionStr="";

	@RequestMapping(value = "/",  method = RequestMethod.GET)
	public String home(Model model) {
		
		
		if(cR!=null)
		{
			model.addAttribute("getAuthorizationCodeFlow", cR.getAuthorizationTokenEndpoint());
			model.addAttribute("getTokenEndpoint", cR.getTokenEndpoint());
			model.addAttribute("getTokenKeysEndpoint", cR.getTokenKeysEndpoint());
			model.addAttribute("getClientId", cR.getClientId());
			model.addAttribute("getClientSecret", cR.getClientSecret());
			model.addAttribute("getScope", cR.getScope());
			//model.addAttribute("getAuthorizationCodeFlow", cR.getAuthorizationCodeFlow());
		}
		
		if(payloadIm!=null)
		{
			model.addAttribute("payloadIm", payloadIm);
		}
		return "Welcome";
	}
	
	@RequestMapping(value = "/expayload", method = RequestMethod.POST)
	@ResponseBody
	public String payloadextract(@RequestBody String payload, HttpSession session) {
		
		try {
			String payloadurl = URLDecoder.decode(payload.substring(8,payload.length()), "UTF-8");
			System.out.println(payloadurl);
			HttpClient httpclient = new HttpClient();
			GetMethod get = new GetMethod(payloadurl);
			try {
				httpclient.executeMethod(get);
				return (get.getResponseBodyAsString()).toString();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    return "NULL";
	}
	
	
	
	@RequestMapping(value = "/exchange",  method = RequestMethod.GET)
	@ResponseBody
	public String exhange(Model model) {
		if(exhangecode==null && payload == null)
			return ""+ "@" + "" + "@" + "";
		System.out.println(payload);
		return exhangecode + "@" + payload + "@" +"Signature Verified" ;
	}
	
	@RequestMapping(value = "/redirectUrl", method = RequestMethod.GET)
		public void redirectUrl(HttpServletResponse httpServletResponse) throws IOException {
		    httpServletResponse.sendRedirect(authUrl);
		}
	
	@JsonView(Views.Public.class)
	@RequestMapping(value = "/startOAuth", method=RequestMethod.POST , consumes={"application/json"})
	@ResponseBody
	public String authenticate(@RequestBody ClientRegistration clientRegistration, HttpSession session) throws ServletException, IOException {
		String responsetype = "code";
		String authtype ;
		System.out.println("Authorization Token Endpoint :" + clientRegistration.getAuthorizationTokenEndpoint());
		System.out.println("Token Endpoint :" + clientRegistration.getTokenEndpoint());
		System.out.println("Token Keys Endpoint :" + clientRegistration.getTokenKeysEndpoint());
		System.out.println("Client Id : " + clientRegistration.getClientId());
		System.out.println("Client secret : " + clientRegistration.getClientSecret());
		System.out.println("Scope : " + clientRegistration.getScope());
		System.out.println("Authorization_Code_Flow : " + clientRegistration.getAuthorizationCodeFlow());	
		session.setAttribute(sessionStr, clientRegistration);
		cR = (ClientRegistration) session.getAttribute(sessionStr);
		System.out.println(cR.getClientId());
		System.out.println(cR.getClientSecret());
		System.out.println(cR.getScope());
		System.out.println(cR.getAuthorizationCodeFlow());
		authtype = cR.getAuthorizationCodeFlow();
		
		if(authtype.equals("Implicit_Code_Flow")){
			responsetype = "token";
			redirectUri = "http://localhost:8080/OIDCClient/";
		}else{
			responsetype = "code";
			redirectUri = "https://localhost:8443/OIDCClient/startOAuth/_callback";
		}
		
		System.out.println(responsetype);
			try {
				authUrl = clientRegistration.getAuthorizationTokenEndpoint() + "?response_type="+ responsetype
						+ "&scope="+ clientRegistration.getScope() + "&client_id=" + cR.getClientId()
						+ "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");
		
			} catch (UnsupportedEncodingException e) {
				throw new ServletException(e);
			}
			return "Success";
	}
	@RequestMapping(value = "/startOAuth/_callback")

	public String authenticateCallback(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String accessToken = null ;
		String userIdToken; 
		String authtype ;
		HttpClient httpclient = new HttpClient();
		GetMethod get;
		
		if (accessToken == null) {
			//Auth method
		    tokenUrl = cR.getTokenEndpoint();
			String code = request.getParameter("code");
			
			PostMethod post = new PostMethod(tokenUrl);
			post.addParameter("code", code);
			post.addParameter("grant_type", "authorization_code");
			post.addParameter("client_id", cR.getClientId());
			post.addParameter("client_secret", cR.getClientSecret());
			post.addParameter("redirect_uri", redirectUri);

			try {
				httpclient.executeMethod(post);
				payloadIm = null;
				try {
					JSONObject authResponse = new JSONObject(
							new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
					System.out.println("Auth response: " + authResponse.toString(2));
					accessToken = authResponse.getString("access_token");
					
					userIdToken = authResponse.getString("id_token");
					System.out.println("Acess Token Acquired" + accessToken);
					System.out.println("ID Token =" + userIdToken);
				
					SignedJWT idToken = null;
					
					try {
						idToken = SignedJWT.parse(userIdToken);
					} catch (ParseException e1) {
						
						e1.printStackTrace();
					}
					String kid = idToken.getHeader().getKeyID();
								
					JWKSet jwks = null;
					try {
						jwks = JWKSet.load(new URL(cR.getTokenKeysEndpoint()));
					} catch (ParseException e) {
					
						e.printStackTrace();
					}
					RSAKey jwk = (RSAKey) jwks.getKeyByKeyId(kid);

					JWSVerifier verifier = null;
					try {
						verifier = new RSASSAVerifier(jwk);
					} catch (JOSEException e) {
						e.printStackTrace();
					}
					try {
						if (idToken.verify(verifier)) {
							
						  System.out.println("ID Token Header: " + idToken.getHeader().toString() );
						  System.out.println("ID Token Payload: " + idToken.getPayload().toString() );
						  System.out.println("ID Token Signature = " + idToken.getSignature().toString());
						  System.out.println("valid signature");
						  
						  exhangecode = code;
						  
						  payload = idToken.getPayload().toString();
						  return "redirect:/";
						} else {
						  System.out.println("invalid signature");
						}
					} catch (JOSEException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			} finally {
				post.releaseConnection();
			}
			
		}
		return "FAILURE";
}
	
}