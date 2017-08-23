<%@page session="false"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<style type="text/css">
/* Adding !important forces the browser to overwrite the default style applied by Bootstrap */
</style>
<style>
nav ul li:first-child { float: left; }
nav ul li { 
  list-style:none;
  margin:10px;
  float: right; 
  padding-right:170px;
}

/* The Modal (background) */
.modal {
	display: none; /* Hidden by default */
	position: fixed; /* Stay in place */
	z-index: 1; /* Sit on top */
	padding-top: 100px; /* Location of the box */
	left: 0;
	top: 0;
	width: 100%; /* Full width */
	height: 100%; /* Full height */
	overflow: auto; /* Enable scroll if needed */
	background-color: rgb(0, 0, 0); /* Fallback color */
	background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
}
/* Modal Content */
.modal-content {
	position: relative;
	background-color: #fefefe;
	margin: auto;
	padding: 0;
	border: 1px solid #888;
	width: 80%;
	box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0
		rgba(0, 0, 0, 0.19);
	-webkit-animation-name: animatetop;
	-webkit-animation-duration: 0.4s;
	animation-name: animatetop;
	animation-duration: 0.4s
}
/* Add Animation */
@
-webkit-keyframes animatetop {
	from {top: -300px;
	opacity: 0
}
to {
	top: 0;
	opacity: 1
}
}
@
keyframes animatetop {
	from {top: -300px;
	opacity: 0
}
to {
	top: 0;
	opacity: 1
}
}
/* The Close Button */
.close {
	color: white;
	float: right;
	font-size: 28px;
	font-weight: bold;
}
.close:hover, .close:focus {
	color: #000;
	text-decoration: none;
	cursor: pointer;
}
.modal-header {
	padding: 2px 16px;
	background-color: #406d95;
	color: white;
}
.modal-body {
	padding: 2px 16px;
}
.modal-footer {
	padding: 2px 16px;
	background-color: #406d95;
	color: white;
}
</style>
<spring:url value="/resources/core/css/bootstrap.min.css"
	var="bootstrapCss" />

<link href="${bootstrapCss}" rel="stylesheet" />
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js">
	
</script>
<spring:url value="/resources/core/gslab_favicon.png" var = "favicon" />
<link rel="shortcut icon" type="image/png" href="${favicon}" />
</head>
<div class="container">
<nav class="navbar" style="background-color:#060a5a;">
		<ul class="title-area"><!-- float this left -->
			<li><a class="navbar-header navbar-brand" href="#"><span style="display: inline-block; color:white;">OIDC-Client</span></a></li>
    		<li><img class="navbar-header navbar-image" src="http://pulse.gslab.com/wp-content/uploads/2017/05/gslogo.png" style="display: inline-block; z-index: 2;position:absolute;height:50px;width:150px;"></li>		
		</ul>
	
</nav>
</div>
<body>
	<div class="container" style="min-height: 50px">
		<script>
			function redirect() {
				window.location = '/OIDCClient/redirectUrl';
			}
		</script>
		<button id="start" onclick='redirect();'>
			Start <span class="glyphicon glyphicon-hand-right"></span>
		</button>
		<button id="config" class="pull-right">
			Configuration <span class="glyphicon glyphicon-cog"></span>
		</button>
		<!-- The Modal -->
		<div id="myModal" class="modal">
			<!-- Modal Content -->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Open ID Connect Configuration</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" id="submit-form" method="post">
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Authorization
								Endpoint:</label>
							<div class="col-sm-10">
								<input type="text" class="form-control"
									id="authorizationTokenEndpoint"
									value="">
							</div>
						</div>
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Token Endpoint:</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="tokenEndpoint"
									value="">
							</div>
						</div>
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Token Keys(JWK)
								Endpoint:</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="tokenKeysEndpoint"
									value="">
							</div>
						</div>
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Client ID:</label>
							<div class="col-sm-10">
								<input type=text class="form-control" id="clientId"
									value="">
							</div>
						</div>
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Client Secret:</label>
							<div class="col-sm-10">
								<input type=text class="form-control" id="clientSecret"
									value="">
							</div>
						</div>

						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">Scope:</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="scope"
									value="">
							</div>
						</div>
						<div class="form-group form-group-md">
							<label class="col-sm-2 control-label">OAuth Flow:</label>
							<div class="col-sm-10">
								<select id="authorization_Code_Flow">
									<option value="Authorization_Code_Flow">Authorization
										Code Flow</option>
									<option value="Implicit_Code_Flow">Implicit Flow</option>
								</select> <br> <span
									class="alert alert-warning glyphicon glyphicon-warning-sign">Open
									In Private Window For Implicit Flow.</span>
							</div>
						</div>
						<p>Remember to set https://oidcclient.gslab.com:8443/OIDCClient/startOAuth/_callback as your callback URL.</p>
					</form>
				</div>
				<div class="modal-footer">
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10">
							<button type="submit" id="btn-submit"
								class="btn btn-primary btn-md">Submit</button>

							<button type="button" class="btn btn-default btn-md"
								data-dismiss="modal" id="modalClose">Close</button>
						</div>
					</div>
					<!-- </form> -->
				</div>
			</div>
		</div>
	</div>

	<div>
		<div class="col-md-offset-2 col-md-8 ">
			<label class="col-md-2 control-label "><span class="label label-info">Auth Code:</span></label>
			<p class="col-md-offset-2 col-sm-offset-2"></p>
			<p class="text-info col-md-offset-2 col-md-2 col-md-offset-3">Your
				Code is:</p>
			<input type="text" class="form-control" id="exchangeToken"
				value="${code}" readonly="readonly" />
		</div>
		<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
		<div id="exchange" class="col-md-offset-5 col-md-2 col-md-offset-5">
			<button type="submit" id="exchangeButton"
				class="btn btn-primary btn-md">Exchange</button>
			
		</div>
		<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
		<p class="text-info col-md-offset-2 col-md-8 col-md-offset-2">Now, we will exchange that access code for an id token,with our server making a request to the token endpoint</p>
		<div class="col-md-offset-2 col-md-8 col-md-offset-2"
			id="authResponse" style="display: none">
			<label class="col-md-2 control-label"><span class="label label-info">Response:</span></label>
			<textarea class="form-control" rows="5" id="requestURL" readonly="readonly"></textarea>
			<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
			<p class="text-info col-md-offset-2 col-md-8 col-md-offset-2">Now, we need to verify that the ID Token sent was from the correct source by validating the JWT's signature</p>
		</div>
		<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
		<div class="col-md-offset-2 col-md-8 col-md-offset-2" id="idToken"
			style="display: none">
			<label class="col-md-2 control-label"><span class="label label-info">Id Token:</span></label>
			<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
			<p class="text-info col-md-offset-2 col-md-8 col-md-offset-2">Your Id_Token is:</p>
			<textarea class="form-control" rows="5" id="id_token" readonly="readonly">${idToken}</textarea>
			<p class="text-info col-md-offset-2 col-md-8 col-md-offset-2">This token is cryptographically signed.We'll fetch and use the Public Key From the Token Keys Endpoint to validate it.</p>
		</div>
		<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
		<p class="col-md-offset-2 col-md-7 col-md-offset-3"></p>
		<div id="verify" class="col-md-offset-5 col-md-2 col-md-offset-5"
			style="display: none">
			<button type="submit" id="verifyButton"
				class="btn btn-primary btn-md">Verify</button>
			<label class="col-md-2 control-label" id="sigveri"></label>
		</div>
		<div class="col-md-offset-2 col-md-8 col-md-offset-2"
			id="tokenHeader" style="display: none">
		<p class="text-info col-md-offset-2 col-md-8 col-md-offset-2">Your Id_Token Header's are:</p>
			<textarea class="form-control" rows="1" id="id_token_header" readonly="readonly"></textarea>
         </div>
		<div class=" col-md-8 col-md-offset-2"
			id="payLoadInput" style="display: none">
			<label class="col-md-2 control-label"><span class="label label-info">Payload:</span></label>
			<textarea class="form-control" rows="5" id="payload" readonly="readonly">${payloadIm}</textarea>
		</div>	
	</div>
	<br>
	<script>
	
	
		$.get("/OIDCClient/getconfig", function(result) {

		}).done(function(result) {
			if (result !="") {
				var str = JSON.parse(JSON.stringify(result));
				$('#authorizationTokenEndpoint').val(str['authorizationTokenEndpoint']);
				$('#tokenEndpoint').val(str['tokenEndpoint']);
				$('#tokenKeysEndpoint').val(str['tokenKeysEndpoint']);
				$('#clientId').val(str['clientId']);
				$('#clientSecret').val(str['clientSecret']);
				$('#scope').val(str['scope']);
				$('#authorization_Code_Flow').val(str['authorizationCodeFlow']);
				
				//find it with what is implicit or authorization.select that in list box using document.getelement.selected etc...
			} else {
				console.log("new session");
			}
		});

		$("#exchange").click(function() {
			$("#authResponse").show();
			$("#idToken").show();
			$("#verify").show();
		});
		$("#verify").click(function() {
			$("#payLoadInput").show();
			$("#tokenHeader").show();
		});
		$("#modalClose").click(function() {
			$("#myModal").hide();
		})
		var modal = document.getElementById('myModal');
		// Get the button that opens the modal
		var btn = document.getElementById("config");
		// Get the <span> element that closes the modal
		var span = document.getElementsByClassName("close")[0];
		// When the user clicks the button, open the modal 
		btn.onclick = function() {
			modal.style.display = "block";
		}
		// When the user clicks on <span> (x), close the modal
		span.onclick = function() {
			modal.style.display = "none";
		}
		// When the user clicks anywhere outside of the modal, close it
		window.onclick = function(event) {
			if (event.target == modal) {
				modal.style.display = "none";
			}
		}
		if (window.location.hash != "") {
			var string = window.location.hash.substr(1);
			$("#requestURL").val(string);
			var query = string.split('&');
			var param;
			var idTokenVal, access_token;
			// Parse the URI hash to fetch the access token
			for (var i = 0; i < query.length; i++) {
				param = query[i].split('=');
				if (param[0] == 'id_token') {
					idTokenVal = param[1];
			$("#exchangeToken").val("");
					$("#verify").show();
					$("#authResponse").show();
					$("#id_token").val(idTokenVal);
					$("#idToken").show();
					$("#id_token_header").val(atob((idTokenVal
							.split('.'))[0]));
					$('#tokenHeader').show();
					break;
				}
			}
			
			$.post('setIdToken', {
						"idTokenResp" : idTokenVal
					})
					.done(
							function(data, status) {
							});
		}
		jQuery(document).ready(function($) {
//			$('#myModal').show();
			
			$("#submit-form").submit(function(event) {
				// Disble the Submit button
				enableSubmitButton(false);
				// Prevent the form from submitting via the browser.
				event.preventDefault();
				submitViaAjax();
			});
		});
		$("#exchangeButton")
				.click(
						function() {
							$
									.get("exchange", function(data, status) {
									})
									.done(
											function(data, status) {
												try {
													var str = JSON.parse(data);
													document.getElementById("requestURL").value = JSON.stringify(str,
																	undefined, 4);
													document.getElementById("id_token").value = str["id_token"];
													document.getElementById("id_token_header").value = atob((str["id_token"]
															.split('.'))[0]);
												} catch(err) {
													$('#requestURL').val(data);
													$('#verify').hide();
													$('#idToken').hide();
													$('#payLoadInput').hide();
;												}

											});
						});
		$("#verifyButton")
				.click(
						function() {
							$
									.get(
											"verify",
											function(data, status) {
												var idnt = "ID TOKEN INVAILD";
												if (idnt != data) {
													var str = JSON.parse(data);
													$("#payload").val(JSON.stringify(str,undefined,4));
													$("#sigveri").val("Signature Verified");
												} else {
													$("#payload").val(idnt);
												}
											});
						});
		$("#btn-submit").click(
				function() {
					var dataString = {}
					dataString["authorizationTokenEndpoint"] = $(
							"#authorizationTokenEndpoint").val();
					dataString["tokenEndpoint"] = $("#tokenEndpoint").val();
					dataString["tokenKeysEndpoint"] = $("#tokenKeysEndpoint")
							.val();
					dataString["clientId"] = $("#clientId").val();
					dataString["clientSecret"] = $("#clientSecret").val();
					dataString["scope"] = $("#scope").val();
					dataString["authorizationCodeFlow"] = $(
							"#authorization_Code_Flow").val();
				
					$.ajax({
						type : "POST",
						headers : {
							'Accept' : 'application/json',
							'Content-Type' : 'application/json'
						},
						contentType : "application/json",
						url : "startOAuth",
						data : JSON.stringify(dataString),
						dataType : 'json',
						timeout : 100000,
						success : function(data) {
							windows.location = data;
							 $('#myModal').hide();
						},
						error : function(e) {
							$('#myModal').hide();
						},
						done : function(e) {
							enableSubmitButton(true);
						}
					})
				});
		function enableSubmitButton(flag) {
			$("#btn-submit").prop("disabled", flag);
		}
		function display(data) {
			var json = "<h4>Ajax Response</h4><pre>"
					+ JSON.stringify(data, null, 4) + "</pre>";
			$('#feedback').html(json);
		}
	</script>

</body>
</html>
