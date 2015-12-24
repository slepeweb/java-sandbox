<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<script>
$(function() {
	var weather = {
			error: "<tr><td></td><td>... not available right now.</td></tr>",
			updateTable: function(html) {
				var table = $("#weather-report table");
				table.empty();
				table.append(html);	
			}
	};
	
	$.ajax({
		url : "/ws/weather/united%20kingdom/cambridge",
		dataType : "json",
		cache : false
	}).done(function(resp) {
		if (resp.status == "Success") {
			html = "<tr><td>Location:</td><td>" + resp.location + "</td></tr>";
			html += "<tr><td>Time:</td><td>" + resp.time + "</td></tr>";
			html += "<tr><td>Temperature:</td><td>" + resp.temperature + "</td></tr>";
			html += "<tr><td>Wind:</td><td>" + resp.wind + "</td></tr>";
			html += "<tr><td>Visibility:</td><td>" + resp.visibility + "</td></tr>";
			html += "<tr><td>Sky:</td><td>" + resp.skyConditions + "</td></tr>";
			html += "<tr><td>Dew point:</td><td>" + resp.dewPoint + "</td></tr>";
			html += "<tr><td>Pressure:</td><td>" + resp.pressure + "</td></tr>";
			weather.updateTable(html);
		}
		else {
			weather.updateTable(weather.error);
		}	
	}).fail(function(jqXHR, status) {
		weather.updateTable(weather.error);
	});		
});
</script>  

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
	<div id="weather-report" <c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
		<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
		<c:if test="${not empty _comp.blurb}"><div>${_comp.blurb}</div></c:if>	
		
		<table><tr><td></td><td>Please wait ...</td></tr></table>
	</div>
</div>
