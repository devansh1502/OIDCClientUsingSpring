<%@page session="false"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<spring:url value="/resources/core/css/bootstrap.min.css"
	var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>

<nav class="navbar navbar-inverse">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">OIDC-Client</a>
		</div>
	</div>
</nav>
<body>
	<div class="container" style="min-height: 50px">
		<script>
			function redirect() {
				window.location = '/OIDCClient/redirectUrl';
			}
		</script>
		<button id="start" onclick='redirect();'>Start</button>
		<button id="config" data-toggle="modal" class="pull-right">
			Configuration <span class="glyphicon glyphicon-cog"></span>
		</button>
		<!-- data-target="#configModal" -->

		<div class="modal fade" id="myModal" role="dialog">
			<div class="modal-dialog">

				<!-- Modal content-->
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
										value="${getAuthorizationTokenEndpoint}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">Token Endpoint:</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="tokenEndpoint"
										value="${getTokenEndpoint}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">Token Keys
									Endpoint:</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="tokenKeysEndpoint"
										value="${getTokenKeysEndpoint}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">Client ID:</label>
								<div class="col-sm-10">
									<input type=text class="form-control" id="clientId"
										value="${getClientId}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">Client Secret:</label>
								<div class="col-sm-10">
									<input type=text class="form-control" id="clientSecret"
										value="${getClientSecret}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">Scope:</label>
								<div class="col-sm-10">
									<input type=text class="form-control" id="scope"
										value="${getScope}">
								</div>
							</div>
							<div class="form-group form-group-md">
								<label class="col-sm-2 control-label">OAuth Flow:</label>
								<div class="col-sm-10">
									<select id="authorization_Code_Flow">
										<option value="Authorization_Code_Flow">Authorization
											Code Flow</option>
										<option value="Implicit_Code_flow">Implicit Code Flow</option>
									</select> <span class="alert alert-warning glyphicon glyphicon-warning-sign">Open In
										Private Window For Implicit Flow.</span>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" id="btn-submit"
									class="btn btn-primary btn-md">Submit</button>

								<button type="button" class="btn btn-default btn-md"
									data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>
		<div class="col-sm-10">
		<label class="col-sm-2 control-label">Auth Code:</label> <input
			type="text" class="form-control" id="exchangeToken" value="${code}" />
	</div>
	<button type="submit" id="exchangeButton"
		class="btn btn-primary btn-md">Exchange</button>
	<label class="col-sm-2 control-label" id="sigveri"></label>

	<div class="col-sm-10">
		<label class="col-sm-2 control-label">Auth Response:</label>
		<textarea class="form-control" rows="5" id="requestURL"></textarea>
	</div>

	<div class="col-sm-10">
		<label class="col-sm-2 control-label">Id Token:</label>
		<textarea class="form-control" rows="5" id="id_token">${idToken}</textarea>
	</div>
	<button type="submit" id="verifyButton" class="btn btn-primary btn-md">Verify</button>
	<div class="col-sm-10">
		<label class="col-sm-2 control-label">Payload:</label>
		<textarea class="form-control" rows="5" id="payload">${payloadIm}</textarea>
	</div>
	<br>	<script>
		var modal = document.getElementById('myModal');

		// Get the button that opens the modal
		var btn = document.getElementById("config");

		// Get the <span> element that closes the modal
		var span = document.getElementsByClassName("close")[0];

		// When the user clicks the button, open the modal 
		/* btn.onclick = function() {
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
		} */

		if (window.location.hash != "") {

			var string = window.location.hash.substr(1);
			$("#requestURL").val(string);
			var query = string.split('&');
			console.log(string);
			var param;
			var idTokenVal, access_token;
			// Parse the URI hash to fetch the access token
			for (var i = 0; i < query.length; i++) {
				param = query[i].split('=');
				/* 	if (param[0] == 'access_token') {
						access_token = param[1];
					} */
				if (param[0] == 'id_token') {
					console.log(param[0]);
					idTokenVal = param[1];
					console.log(param[1]);
					break;
				}
			}
			$("#id_token").val(idTokenVal);
			$.post('setIdToken', {
				"idTokenResp" : idTokenVal
			}).done(function(data, status) {
				console.log("response = " + data);
			});

			/* var urlsend = decodeURIComponent(idurl) + '?access_token=' + access_token;
			console.log("hello"); */

			/* $.post("/OIDCClient/expayload", {
				payload : urlsend
			}, function(data, status) {
				var str = JSON.parse(data);
				document.getElementById("payload").value = JSON.stringify(str,undefined,4);
			});
			 */
		}

		$(function() {
			//console.log("ready");
			$('#config').click(function() {
				//console.log("config clicked.");
				$('#configModal').modal('toggle');
			});

			$("#submit-form").submit(function(event) {

				// Disable the submit button
				enableSubmitButton(false);

				// Prevent the form from submitting via the browser.
				event.preventDefault();

				submitViaAjax();

			});

		});
		$("#exchangeButton")
		.click(
				function() {
					$.get("exchange", function(data, status) {

					}).done(function(data, status) {
										var str = JSON.parse(data);
										document.getElementById("requestURL").value = JSON.stringify(str,undefined, 4);
										document.getElementById("id_token").value = str["id_token"];
									});
				});

		$("#verifyButton")
		.click(
				function() {
					$
							.get(
									"verify",
									function(data, status) {
										var str = JSON.parse(data);
										document
												.getElementById("payload").value = JSON
												.stringify(str,
														undefined, 4);
										document
												.getElementById("sigveri").value = "Signature Verified";
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
					console.log(JSON.stringify(dataString));
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
							console.log("SUCCESS: ", data);
							$('#configModal').modal('toggle');
							//display(data);
						},
						error : function(xhr, status, error) {
							console.log("ERROR:");
							console.log(xhr);
							console.log(status);
							console.log(error);
							$('#configModal').modal('toggle');
							//display(e);
						},
						done : function(e) {
							console.log("DONE");
							//enableSubmitButton(true);
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