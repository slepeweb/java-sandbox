$(function() {

	if (_isPasswordClient) {
		$("tr.hide").removeClass("hide");
	}

	$("#password-update").click(
			function(e) {
				$("#password-pwd").val("");
				var org = $("#password-org").val();
				var key = $("#password-key").val();
				if (org) {
					$.ajax({
						url : "/ws/password?org=" + org + "&key=" + key,
						contentType : "application/json",
						dataType : "json",
						cache : false
					}).done(function(resp) {
						$("#password-results").removeClass("hide");
						$("#password-pwd").val(resp.password);
						$("#password-chunked").val(resp.chunked);
					}).fail(
							function(jqXHR, status, error) {
								alert("jqXHR: " + jqXHR.status + "\nstatus: "
										+ status + "\nerror: " + error);
							});
				} else {
					window.alert("Please supply a key");
				}
			});

	$("#password-org, #password-key").click(function(e) {
		$("#password-pwd").val("");
		$("#password-chunked").val("");
	});

	$("#password-reset").click(function(e) {
		$("#password-org").val("");
		$("#password-key").val("");
		$("#password-pwd").val("");
		$("#password-chunked").val("");
		$("#password-results").addClass("hide");
	});


});
