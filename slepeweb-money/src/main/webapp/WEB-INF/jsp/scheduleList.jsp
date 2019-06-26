<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<div class="right">
		<a href="${_ctxPath}/schedule/add">New schedule</a><br />
	</div>

	<h2>Scheduled Transactions <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<c:choose><c:when test="${fn:length(_scheduled) > 0}">
		<table>
			<tr>
				<th>Name</th>
				<th>Day of month</th>
				<th>Last entered</th>
			</tr>
			
			<c:forEach items="${_scheduled}" var="_schedule">
				<tr>
					<td><a href="${_ctxPath}/schedule/edit/${_schedule.id}">${_schedule.label}</a></td>
					<td>${_schedule.day}</td>
					<td>${_schedule.entered}</td>
				</tr>
			</c:forEach>			
		</table>
					
	</c:when><c:otherwise>
		<p><strong>No scheduled transactions</strong></p>
	</c:otherwise></c:choose>
	
	<script>
		$(function() {
			$(".fa-caret-square-right").click(function (e) {
				var params = $(this).attr("data-params");
				var type = $(this).attr("data-type");
				window.location = webContext + "/search/save/repeat?type=" + type + "&json=" + params;
			});	
			
			$("#accordion-ss").accordion({
				active: false,
				collapsible: true,
				heightStyle: content
			});
		});
	</script>
	
</mny:standardLayout>
