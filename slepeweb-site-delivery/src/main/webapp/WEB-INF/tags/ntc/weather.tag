<%@ tag %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
$(function() {
	var header = "<span>Local weather</span> ";
	var weather = {
			error: "... not available right now.",
			updateSpan: function(line1, line2) {
				var span = $("#weather-report p");
				span.empty();
				span.append(line1 + "<br />" + line2);	
			}
	};
	
	$.ajax({
		url : "/ws/weatherw/united%20kingdom/cambridge",
		dataType : "json",
		cache : false
	}).done(function(resp) {
		if (resp.status == "Success") {
			var line1 = header + "@ " + resp.time;
			var html = resp.temperature + ", ";
			html += resp.wind + ", ";
			html += resp.skyConditions + ", ";
			html += resp.humidity + " humidity";
			//html += "<tr><td>Visibility:</td><td>" + resp.visibility + "</td></tr>";
			//html += "<tr><td>Sky:</td><td>" + resp.skyConditions + "</td></tr>";
			//html += "<tr><td>Dew point:</td><td>" + resp.dewPoint + "</td></tr>";
			//html += "<tr><td>Pressure:</td><td>" + resp.pressure + "</td></tr>";
			weather.updateSpan(line1, html);
		}
		else {
			weather.updateSpan(header, weather.error);
		}	
	}).fail(function(jqXHR, status) {
		weather.updateSpan(header, weather.error);
	});		
});
</script>  

<div id="weather-report">
	<p>Local weather: Please wait ...</p>
</div>
