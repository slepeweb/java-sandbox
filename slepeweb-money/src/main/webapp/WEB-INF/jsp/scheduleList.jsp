<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInPageCss" scope="request">
	td.enabled-flag {
		font-weight: bold;
		font-size: 1.2em;
	}
</c:set>

<mny:standardLayout>

	<mny:pageHeading heading="Scheduled transactions ">
		<a href="${_ctxPath}/schedule/add" title="Define a new scheduled transaction">New schedule</a>
	</mny:pageHeading>

	<c:choose><c:when test="${fn:length(_scheduled) > 0}">
		<table>
			<thead>
				<tr>
					<th>Enabled</th>
					<th>Name</th>
					<th>Next date</th>
					<th>Account</th>
					<th>Amount</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${_scheduled}" var="_schedule">
					<tr>
						<td class="enabled-flag">${mon:tertiaryOp(_schedule.enabled, '*', '')}</td>
						<td><a href="${_ctxPath}/schedule/edit/${_schedule.id}"
							title="Update the details for this scheduled transaction">${_schedule.label}</a></td>
						<td>${mon:formatTimestamp(_schedule.nextDate)}</td>
						<td>${_schedule.account.name}</td>
						<td class="currency amount">${mon:displayAmountNS(_schedule.amount)}</td>
					</tr>
				</c:forEach>
			</tbody>		
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
		});
	</script>
	
</mny:standardLayout>
