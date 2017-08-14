<%@page session="false"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<spring:url value="/resources/core/css/bootstrap.min.css"
	var="bootstrapCss" />
<link href="resources/core/css/bootstrap.min.css" rel="stylesheet" />

<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<%-- <spring:url value="/resources/core/js/jquery.1.10.2.min.js"
	var="jqueryJs" />
<script src="${jqueryJs}"></script> --%>
</head>

<nav class="navbar navbar-inverse">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">OIDC-Client</a>
		</div>
	</div>
</nav>
<body>
	<div class="container" style="min-height: 500px">
		<script>
			function fillInAttributes() {
				alert('@Request.RequestContext.HttpContext.Session["clientId"]');
				document.getElementById('authorizationTokenEndpoint').innerHTML=cR.getAttribute("authorizationTokenEndpoint");
				
				document.getElementById('tokenEndpoint').innerHTML=cR.getAttribute("tokenEndpoint");
				document.getElementById('tokenKeysEndpoint').innerHTML=cR.getAttribute("tokenKeysEndpoint");
				document.getElementById('clientId').innerHTML=cR.getAttribute("clientId");
				document.getElementById('clientSecret').innerHTML=cR.getAttribute("clientSecret");
				document.getElementById('scope').innerHTML=cR.getAttribute("scope");
				document.getElementById('authorization_Code_Flow').innerHTML=cR.getAttribute("authorizationCodeFlow");
				
			}
			function redirect() {
				window.location = '/OIDCClient/redirectUrl';
				alert("as");
				fillInAttributes();
			}
		</script>
		<button id="start" onclick='redirect();'>start</button>
		<button id="config">config</button>
		<div id="myModal" class="modal">
			<div class="starter-template">
				<br> <br> <br> <br> <br> <br> <span
					class="close">&times;</span>
				<form class="form-horizontal" id="submit-form" method="post">
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Authorization Token
							Endpoint:</label>
						<div class="col-sm-10">
							<input type="text" class="form-control"
								id="authorizationTokenEndpoint">
						</div>
					</div>
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Token Endpoint:</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="tokenEndpoint">
						</div>
					</div>
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Token Keys Endpoint:</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="tokenKeysEndpoint">
						</div>
					</div>
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Client ID:</label>
						<div class="col-sm-10">
							<input type=text class="form-control" id="clientId">
						</div>
					</div>
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Client Secret:</label>
						<div class="col-sm-10">
							<input type=text class="form-control" id="clientSecret">
						</div>
					</div>

					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Scope:</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="scope">
						</div>
					</div>
					<div class="form-group form-group-lg">
						<label class="col-sm-2 control-label">Code Flow:</label>
						<div class="col-sm-10">
							<select id="authorization_Code_Flow">
								<option value="Authorization_Code_Flow">Authorization
									Code Flow</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10">
							<button type="submit" id="btn-submit"
								class="btn btn-primary btn-lg">Submit</button>
						</div>
					</div>
				</form>
				<!-- <form action ="/startOAuth" method="post">
			<input id="submit" type="submit" value="Submit">
			</form>
	 -->
			</div>
		</div>

	</div>
	<div class="col-sm-10">
		<label class="col-sm-2 control-label">Exchange Token: </label> <input
			type="text" class="form-control" id="exchangeToken" value="" />
	</div>
	<button type="submit" id="exchangeButton"
		class="btn btn-primary btn-md">Exchange</button>
	<label class="col-sm-2 control-label" id="sigveri"></label>
	<div class="col-sm-10">
		<label class="col-sm-2 control-label">Payload: </label>
		<textarea class="form-control" rows="5" id="payload"></textarea>
	</div>
	<br>

	<script>
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
		jQuery(document).ready(function($) {
			$("#submit-form").submit(function(event) {
				// Disble the search button
				enableSubmitButton(false);
				// Prevent the form from submitting via the browser.
				event.preventDefault();
				submitViaAjax();
			});
		});

		$("#exchangeButton").click(function() {
			$.get("exchange", function(data, status) {
				var json = (data).split("@");
				document.getElementById("exchangeToken").value = json[0];
				document.getElementById("payload").value = json[1];
				document.getElementById("sigveri").innerHTML = json[2];
			});
		});

		function submitViaAjax() {
			var dataString = {}
			dataString["authorizationTokenEndpoint"] = $(
					"#authorizationTokenEndpoint").val();
			dataString["tokenEndpoint"] = $("#tokenEndpoint").val();
			dataString["tokenKeysEndpoint"] = $("#tokenKeysEndpoint").val();
			dataString["clientId"] = $("#clientId").val();
			dataString["clientSecret"] = $("#clientSecret").val();
			dataString["scope"] = $("#scope").val();
			dataString["authorizationCodeFlow"] = $("#authorization_Code_Flow")
					.val();
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
					windows.location = data;
					console.log("SUCCESS: ", data);
					display(data);

				},
				error : function(e) {
					console.log("ERROR: ", e);
					display(e);
				},
				done : function(e) {
					console.log("DONE");
					enableSearchButton(true);
				}
			});
		}
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