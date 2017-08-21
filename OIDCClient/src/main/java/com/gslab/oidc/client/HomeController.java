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
import java.util.Date;
import java.util.logging.Logger;

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
import com.gslab.oidc.constants.VerificationConstants;
import com.gslab.oidc.jsonViews.Views;
import com.gslab.oidc.model.ClientRegistration;
import com.gslab.oidc.model.ResponseVerification;	
import com.nimbusds.jose.JOSEException;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject.*;


@Controller
public class HomeController {

	
	ClientLogging log = new ClientLogging();
	private String redirectUri = "https://oidcclient.gslab.com:8443/OIDCClient/startOAuth/_callback";

	private String authUrl = null;
	private BigInteger nonce = null;
	String sessionExchangeCode = "";

	private String userIdToken = null;
	private ClientRegistration cR = null;
	HttpSession newsession;
	private static final String ID_TOKEN_INV = "ID TOKEN INVAILD";
	private static final String CLIENT_REG_SESS_ATTR = "clientRegistration";
	private static final String AUTH_CODE_SESS_ATTR = "authCode";
	private static final String ID_TOKEN_SESS_ATTR = "idToken";
	Logger logger = log.logger;
     
    
	/**
	 * This Method fetchs Client Registration Json data for the Welcome.jsp
	 * 
	 * @param session
	 * @return ClientRegistration
	 */	
	@RequestMapping(value = "/getconfig", method = RequestMethod.GET)
	@ResponseBody
	public ClientRegistration getconfig(HttpSession session) {	
		ClientRegistration cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		//logger.info(AUTH_CODE_SESS_ATTR);
		if (cR != null) {
		return cR;
		}
		return null;
	}
	
	/**
	 * This Method invokes Welcome.jsp page for Client to Fetch/fill client data
	 * and also set's the Exchange Code when Client's Callback is called
	 * 
	 * @param Model
	 *            Model class containing the attributes required for managing
	 *            the client registration details.
	 * @param session
	 * @return String
	 */	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, HttpSession session) {
		// get ClientRegistration from current session of Client

        // This block configure the logger with handler and formatter  
		ClientRegistration cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		userIdToken = null;
		//logger.info("welcome");
		if (cR != null) {
			
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

	/**
	 * This Method ensure's that Auth/Implicit Mode the ID_TOKEN is set for 
	 * verification in Client's Session
	 * 
	 * @param idTokenResp
	 *            Response from client Side to Set the attribute.
	 * @param session
	 * @return String
	 */
	@RequestMapping(value = "/setIdToken", method = RequestMethod.POST)
	@ResponseBody
	public String setIdToken(@RequestBody String idTokenResp, HttpSession session) {
		logger.info("idTokenResponse = " + idTokenResp);
		session.setAttribute(ID_TOKEN_SESS_ATTR, idTokenResp.split("=")[1]);
		return "SUCCESS";
	}

	/**
	 * This Method Extract payload for Implicit mode and handles if fetching is failed.
	 * 
	 * @param payload
	 *            Response Payload Url from Welcome.jsp for Extracting specific data of user.
	 * @param session
	 * @return String
	 */
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

		return "Your response is not recieved, Try Again.";
	}

	/**
	 * This Method verifies Auth response on the Backend Side using 12 Steps Verifications
	 * and also Works for any certain signature algorithms
	 * 
	 * @param Model
	 *            Model class containing the attributes required for managing
	 *            the client registration details.
	 * @param session
	 * @return String
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	@ResponseBody
	public String verify(HttpSession session) {

		SignedJWT idToken = null;
		ResponseVerification vresp = new ResponseVerification();
		Date today = new Date();
		
		ClientRegistration cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		
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
		
		//Verifications of 12 Steps except azp 
		
		net.minidev.json.JSONObject authResponse;
		authResponse = idToken.getPayload().toJSONObject();
		
		vresp.setIss(authResponse.get(VerificationConstants.ISSUER).toString());
		vresp.setAud(authResponse.get(VerificationConstants.AUD).toString());
		vresp.setExp(((long)(authResponse.get(VerificationConstants.EXP))*1000));
		vresp.setNonce_resp(authResponse.get(VerificationConstants.NONCE).toString());
		vresp.setIat(((long) (authResponse.get(VerificationConstants.IAT)) *1000));
		
		String issmatch = null ;
		
		try {
			System.out.println(cR.getAuthorizationTokenEndpoint());
			URL aURL = new URL((cR.getAuthorizationTokenEndpoint()));
			issmatch = aURL.getHost();
			if(!vresp.getIss().contains(issmatch))
			{
				System.out.println("iss is wrong ");
				return ID_TOKEN_INV; 
			}
			if(!vresp.getAud().equals(cR.getClientId())){
				System.out.println("client id is wrong ");
				return ID_TOKEN_INV; 
			}
			if(vresp.getIat()>today.getTime()){
				System.out.println("JWT arrival Passed already");
				return ID_TOKEN_INV;
			}
			
			if(vresp.getExp()<today.getTime()){
				System.out.println("Expired Already");
				return ID_TOKEN_INV;
			}
			
			if(!(vresp.getNonce_resp().equals(nonce.toString(16))))
			{
				System.out.println("nonce invalid");
				return ID_TOKEN_INV; 
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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

		return ID_TOKEN_INV;
	}

	// OnClicking the Exchange Button this Method Ensure's to fetch Auth
	// Response and Get Id_token to verify it further
	/**
	 * This Method invokes Exchange Auth response parameters with Vendors Info
	 * 
	 * @param session
	 * @return String
	 */
	@RequestMapping(value = "/exchange", method = RequestMethod.GET)
	@ResponseBody
	public String exchange(HttpSession session) {

		String accessToken = null;

		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(cR.getTokenEndpoint());

		// Add's Specific Parameters to fetch Auth Response
		post.addParameter(QueryParamConstants.CODE, session.getAttribute(AUTH_CODE_SESS_ATTR).toString());
		post.addParameter("grant_type", "authorization_code");
		post.addParameter(QueryParamConstants.CLIENT_ID, cR.getClientId());
		post.addParameter("client_secret", cR.getClientSecret());
		post.addParameter(QueryParamConstants.REDIRECT_URL, redirectUri);
		
		JSONObject authResponse = null;
		try {

			httpclient.executeMethod(post);

			
			try {
				authResponse = new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
				System.out.println("===========Exchange Auth Response===========");
				System.out.println("Auth response: " + authResponse.toString(2));
				System.out.println("===========Exchange Auth Response===========");
				
				accessToken = authResponse.getString("access_token");
				userIdToken = authResponse.getString("id_token");

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
		return "Your Auth have incorrect parameters, try again." + '\n' +  authResponse.toString();
	}

	// This Method Ensure's Redirection's of browser to oAuth Vendor to
	// Sign-In/Login
	
	/**
	 * This Method Ensure's redirection of browser to oauth2.0 Vendor for Login
	 * 
	 * @param httpServletResponse
	 *            Response is redirected to Authorization URL for client-side
	 * @return void
	 * @throws IOException
	 */
	@RequestMapping(value = "/redirectUrl", method = RequestMethod.GET)
	public void redirectUrl(HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.sendRedirect(authUrl);
	}

	/**
	 * This Method set's the current session for Client-side and make AuthUrl for start
	 * 
	 * @param clientRegistration
	 *            CLient class containing the attributes required for managing
	 *            the client registration details.
	 * @param session
	 * @return String
	 * @throws ServletException
	 * @throws IOException
	 */
	@JsonView(Views.Public.class)
	@RequestMapping(value = "/startOAuth", method = RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody
	public String authenticate(@RequestBody ClientRegistration clientRegistration, HttpSession session)
			throws ServletException, IOException {
		String responsetype = QueryParamConstants.CODE;
		String authtype;

		logger.info("===========Submit Button Log===========");
		logger.info("Authorization Token Endpoint :" + clientRegistration.getAuthorizationTokenEndpoint());
		logger.info("Token Endpoint :" + clientRegistration.getTokenEndpoint());
		logger.info("Token Keys Endpoint :" + clientRegistration.getTokenKeysEndpoint());
		logger.info("Scope : " + clientRegistration.getScope());
		logger.info("Authorization_Code_Flow : " + clientRegistration.getAuthorizationCodeFlow());
		logger.info("===========Submit Button Log===========");

		// Make Sure that Client get's its session with specified data
		session.setAttribute(CLIENT_REG_SESS_ATTR, clientRegistration);

		System.out.println(CLIENT_REG_SESS_ATTR);
		cR = (ClientRegistration) session.getAttribute(CLIENT_REG_SESS_ATTR);
		authtype = cR.getAuthorizationCodeFlow();

		// Decides implicit's or Auth Flow
		if (authtype.equals(QueryParamConstants.IMPLICT_FLOW)) {
			responsetype = QueryParamConstants.TOKEN_ID;
			// redirectUri = "https://oidcclient.gslab.com:8443/OIDCClient/";
			redirectUri = "https://oidcclient.gslab.com:8443/OIDCClient/startOAuth/_callback";
		} else {
			responsetype = "code";
			redirectUri = "https://oidcclient.gslab.com:8443/OIDCClient/startOAuth/_callback";
		}
	
		nonce = new BigInteger(50, new SecureRandom());
		
		try {
			authUrl = clientRegistration.getAuthorizationTokenEndpoint() + "?"+QueryParamConstants.RESPONSE_TYPE+"=" + responsetype + "&"+QueryParamConstants.SCOPE+"="
					+ clientRegistration.getScope() + "&" + QueryParamConstants.CLIENT_ID+"=" + cR.getClientId() + "&" + QueryParamConstants.NONCE
					+ "=" + nonce.toString(16) + "&"+QueryParamConstants.REDIRECT_URL+"=" + URLEncoder.encode(redirectUri, "UTF-8");

			logger.info("===========Auth Type & Auth URL===========");
			logger.info("Auth Type: " + responsetype + " Auth Url:  " + authUrl);
			logger.info("===========Auth Type & Auth URL===========");

		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		return "Success";
	}

	/**
	 * This Method is invoked as callback function for Auth. URL 
	 * and saves Session for Exchange Code.
	 * 
	 * @param request
	 * @param response
	 * @return String
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/startOAuth/_callback")
	public String authenticateCallback(HttpServletRequest request, HttpServletResponse response, Model model)
			throws ServletException, IOException {

		String excode = request.getParameter("code");
	System.out.println(excode);
		// Set's Exchange Token to Client Session's
		if (excode != null) {
			request.getSession().setAttribute(AUTH_CODE_SESS_ATTR, excode);
			System.out.println("Exchange Code: " + excode);
		}
		return "redirect:/";
	}

}
