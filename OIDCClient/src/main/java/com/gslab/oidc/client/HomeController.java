package com.gslab.oidc.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
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
import com.gslab.oidc.constants.QueryParamConstants;
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
	private String exchangecode = null;
	private String payload = null;
	private String nonce = null;
	String sessionExchangeCode = "";
	private String payloadIm = null;
	private String code = null;
	private String userIdToken = null;
	private ClientRegistration cR = null;
	HttpSession newsession;
	private static final String CLIENT_REG_SESS_ATTR = "clientRegistration";
	private static final String AUTH_CODE_SESS_ATTR = "authCode";
	private static final String ID_TOKEN_SESS_ATTR = "idToken";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, HttpSession session) {
		// get ClientRegistration from current session of Client
		ClientRegistration cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		userIdToken = null;
		if (cR != null) {
			model.addAttribute("getAuthorizationTokenEndpoint", cR.getAuthorizationTokenEndpoint());
			model.addAttribute("getTokenEndpoint", cR.getTokenEndpoint());
			model.addAttribute("getTokenKeysEndpoint", cR.getTokenKeysEndpoint());
			model.addAttribute("getClientId", cR.getClientId());
			model.addAttribute("getClientSecret", cR.getClientSecret());
			model.addAttribute("getScope", cR.getScope());
			model.addAttribute("getAuthorizationCodeFlow", cR.getAuthorizationCodeFlow());

			if (session.getAttribute(AUTH_CODE_SESS_ATTR) != null) {
				// Only Set's Value if Exchange Code is given for a Session
				// Client
				model.addAttribute("code", session.getAttribute(AUTH_CODE_SESS_ATTR));
			}
			if (session.getAttribute(ID_TOKEN_SESS_ATTR) != null) {
				model.addAttribute("idToken", session.getAttribute(ID_TOKEN_SESS_ATTR));
			}

		}

		return "Welcome";
	}

	@RequestMapping(value = "/setIdToken", method = RequestMethod.POST)
	@ResponseBody
	public String setIdToken(@RequestBody String idTokenResp, HttpSession session) {
		System.out.println("idTokenResponse = " + idTokenResp);
		session.setAttribute(ID_TOKEN_SESS_ATTR, idTokenResp.split("=")[1]);
		return "SUCCESS";
	}

	@RequestMapping(value = "/expayload", method = RequestMethod.POST)
	@ResponseBody
	public String payloadextract(@RequestBody String payload, HttpSession session) {

		try {

			// Extract the Payload URL from #accessToken=..&Id=.... for
			// retriving the Payload Data
			String payloadurl = URLDecoder.decode(payload.substring(8, payload.length()), "UTF-8");

			System.out.println("===========IMPLICIT Payload URL===========");
			System.out.println(payloadurl);
			System.out.println("===========IMPLICIT Payload URL===========");

			HttpClient httpclient = new HttpClient();

			GetMethod get = new GetMethod(payloadurl);

			try {
				httpclient.executeMethod(get);

				System.out.println("===========IMPLICIT Response Data===========");
				System.out.println(get.getResponseBodyAsString());
				System.out.println("===========IMPLICIT Response Data===========");
				// Giving Response Back to Client for Payload
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

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	@ResponseBody
	public String verify(Model model, HttpSession session) {

		SignedJWT idToken = null;

		try {
			System.out.println("userIdToken = " + userIdToken);
			if (userIdToken == null || userIdToken.length() <= 0) {
				System.out.println("idTokenFromSession = " + session.getAttribute(ID_TOKEN_SESS_ATTR).toString());
				userIdToken = session.getAttribute(ID_TOKEN_SESS_ATTR).toString();
			}
			idToken = SignedJWT.parse(userIdToken);
		} catch (ParseException e1) {

			e1.printStackTrace();
		}
		// Extract Key ID from the Current ID Token
		String kid = idToken.getHeader().getKeyID();

		JWKSet jwks = null;
		try {

			try {
				// Load the Key Id's From the Token Key URL
				jwks = JWKSet.load(new URL(cR.getTokenKeysEndpoint()));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		// Match the Key Id of ID token with Loaded Key Id's of Token Key URL
		RSAKey jwk = (RSAKey) jwks.getKeyByKeyId(kid);

		JWSVerifier verifier = null;
		try {
			verifier = new RSASSAVerifier(jwk);
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		try {
			// Verify That Signature matches out with payload using Signing
			// Algorithms
			if (idToken.verify(verifier)) {
				System.out.println("=========== AUTH_ID_TOKEN===========");
				System.out.println("ID Token Header: " + idToken.getHeader().toString());
				System.out.println("ID Token Payload: " + idToken.getPayload().toString());
				System.out.println("ID Token Signature = " + idToken.getSignature().toString());
				System.out.println("valid signature");
				System.out.println("===========AUTH_ID_TOKEN===========");

				return idToken.getPayload().toString();

			}
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "UNVERIFED";
	}

	// OnClicking the Exchange Button this Method Ensure's to fetch Auth
	// Response and Get Id_token to verify it further
	@RequestMapping(value = "/exchange", method = RequestMethod.GET)
	@ResponseBody
	public String exchange(Model model, HttpSession session) {

		String accessToken = null;

		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(cR.getTokenEndpoint());

		// Add's Specific Parameters to fetch Auth Response
		post.addParameter("code", session.getAttribute(AUTH_CODE_SESS_ATTR).toString());
		post.addParameter("grant_type", "authorization_code");
		post.addParameter("client_id", cR.getClientId());
		post.addParameter("client_secret", cR.getClientSecret());
		post.addParameter("redirect_uri", redirectUri);

		try {

			httpclient.executeMethod(post);

			JSONObject authResponse;
			try {
				authResponse = new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));

				accessToken = authResponse.getString("access_token");
				userIdToken = authResponse.getString("id_token");

				System.out.println("===========Exchange Auth Response===========");
				System.out.println("Auth response: " + authResponse.toString(2));
				System.out.println("===========Exchange Auth Response===========");

				return authResponse.toString();
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		} catch (HttpException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		return "NULL";
	}

	// This Method Ensure's Redirection's of browser to oAuth Vendor to
	// Sign-In/Login
	@RequestMapping(value = "/redirectUrl", method = RequestMethod.GET)
	public void redirectUrl(HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.sendRedirect(authUrl);
	}

	/**
	 * This Method works with Onclick of Submit Button in Config to ensure
	 * Client Registration is loaded in session
	 * 
	 * @param clientRegistration
	 *            Model class containing the attributes required for managing
	 *            the client registration details.
	 * @param session
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@JsonView(Views.Public.class)
	@RequestMapping(value = "/startOAuth", method = RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody
	public String authenticate(@RequestBody ClientRegistration clientRegistration, HttpSession session)
			throws ServletException, IOException {
		String responsetype = "code";
		String authtype;

		System.out.println("===========Submit Button Log===========");
		System.out.println("Authorization Token Endpoint :" + clientRegistration.getAuthorizationTokenEndpoint());
		System.out.println("Token Endpoint :" + clientRegistration.getTokenEndpoint());
		System.out.println("Token Keys Endpoint :" + clientRegistration.getTokenKeysEndpoint());
		System.out.println("Client Id : " + clientRegistration.getClientId());
		System.out.println("Client secret : " + clientRegistration.getClientSecret());
		System.out.println("Scope : " + clientRegistration.getScope());
		System.out.println("Authorization_Code_Flow : " + clientRegistration.getAuthorizationCodeFlow());
		System.out.println("===========Submit Button Log===========");

		// Make Sure that Client get's its session with specified data
		session.setAttribute(CLIENT_REG_SESS_ATTR, clientRegistration);

		System.out.println(CLIENT_REG_SESS_ATTR);
		cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		authtype = cR.getAuthorizationCodeFlow();

		// Decides implicit's or Auth Flow
		if (authtype.equals("Implicit_Code_Flow")) {
			responsetype = "token id_token";
			// redirectUri = "https://localhost:8443/OIDCClient/";
			redirectUri = "https://localhost:8443/OIDCClient/startOAuth/_callback";
		} else {
			responsetype = "code";
			redirectUri = "https://localhost:8443/OIDCClient/startOAuth/_callback";
		}
		nonce = new BigInteger(50, new SecureRandom()).toString(16);
		try {
			authUrl = clientRegistration.getAuthorizationTokenEndpoint() + "?response_type=" + responsetype + "&scope="
					+ clientRegistration.getScope() + "&client_id=" + cR.getClientId() + "&" + QueryParamConstants.NONCE
					+ "=" + nonce + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");

			System.out.println("===========Auth Type & Auth URL===========");
			System.out.println("Auth Type: " + responsetype + " Auth Url:  " + authUrl);
			System.out.println("===========Auth Type & Auth URL===========");

		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		return "Success";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/startOAuth/_callback")
	public String authenticateCallback(HttpServletRequest request, HttpServletResponse response, Model model)
			throws ServletException, IOException {

		String excode = request.getParameter("code");
		// Set's Exchange Token to Client Session's
		if (excode != null) {
			request.getSession(false).setAttribute(AUTH_CODE_SESS_ATTR, excode);
			System.out.println("Exchange Code: " + excode);
		}
		return "redirect:/";
	}

}