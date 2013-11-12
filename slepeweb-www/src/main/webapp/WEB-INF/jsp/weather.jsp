<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
$(function() {
	$.ajax({
		url : "/ws/weather/united%20kingdom/cambridge",
		dataType : "json",
		cache : false
	}).done(function(resp) {
		var html;
		if (resp.status == "Success") {
			html = "<tr><td>Location:</td><td>" + resp.location + "</td></tr>";
			html += "<tr><td>Time:</td><td>" + resp.time + "</td></tr>";
			html += "<tr><td>Temperature:</td><td>" + resp.temperature + "</td></tr>";
			html += "<tr><td>Wind:</td><td>" + resp.wind + "</td></tr>";
			html += "<tr><td>Visibility:</td><td>" + resp.visibility + "</td></tr>";
			html += "<tr><td>Sky:</td><td>" + resp.skyConditions + "</td></tr>";
			html += "<tr><td>Dew point:</td><td>" + resp.dewPoint + "</td></tr>";
			html += "<tr><td>Pressure:</td><td>" + resp.pressure + "</td></tr>";
		}
		else {
			html = "<tr><td></td><td>... not available right now.</td></tr>";
		}
	
		var table = $("#weather-report");
		table.empty();
		table.append(html);
		
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
});
</script>  

<div>
	<h3>Local weather</h3>
	<table id="weather-report" class="compact"><tr><td></td><td>Please wait ...</td></tr></table>
</div>     