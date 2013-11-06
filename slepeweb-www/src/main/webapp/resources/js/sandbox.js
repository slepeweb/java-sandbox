$(function() {
	$("#tabs").tabs( 
	  //{active: 0}
	);
	
	$("#lotteryrefresh").click(function(e) {		
		$.ajax({
			url : "/rest/lottery/numbers/3",
			dataType : "json",
			cache : false
		}).done(function(resp) {
			var html = "<table class='compact' width='450px' border='1'><tr>";
			for ( var i = 0; i < resp.lines.length; i++) {
				html += "<td width='33%'><u>" + resp.lines[i] + "</u></td>";
			}
			html += "</tr></table>";
			$("#lotterynumbers").append(html);
		}).fail(function(jqXHR, status) {
			//console.log(status);
		});		
	});
	
	$("#lotteryclear").click(function(e) {
		$("#lotterynumbers").empty();
	});
	
	$("#lotteryrefresh").click();


	$("#password-update").click(function(e) {
		$("#password-pwd").val("");
		var org = $("#password-org").val();
		if (org) {
			$.ajax({
				url : "/ws/password?org=" + org,
				contentType : "application/json",
				dataType : "json",
				cache : false
			}).done(function(resp) {
				$("#password-pwd").val(resp.password);
			}).fail(function(jqXHR, status, error) {
				alert("jqXHR: " + jqXHR.status + "\nstatus: " + status + "\nerror: " + error);
			});		
		}
		else {
			window.alert("Please supply a key");
		}
	});
	
	$("#password-org").click(function(e) {
		$("#password-pwd").val("");
	});
});	
