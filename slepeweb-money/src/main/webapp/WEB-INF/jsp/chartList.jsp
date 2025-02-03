<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>

	<mny:pageHeading heading="Chart list">
		<a href="${_ctxPath}/chart/add" title="Define and save new chart critera">New chart</a>
	</mny:pageHeading>

	<c:choose><c:when test="${not empty _charts}">
		<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Date created</th>
					<th>Execute</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${_charts}" var="_ss">
					<tr>
						<td><a href="${_ctxPath}/chart/edit/${_ss.id}" 
							title="${mon:renderEither(_ss.description, 'Click to update the search parameters for this chart')}">${_ss.name}</a></td>
						<td>${_ss.savedWithMinutes}</td>
						<td><i class="far fa-caret-square-right" title="Execute the search and plot the results"
							data-id="${_ss.id}"></i></td>
					</tr>
				</c:forEach>
			</tbody>			
		</table>					
	</c:when><c:otherwise>
		<p><strong>No saved charts</strong></p>
	</c:otherwise></c:choose>
	
	<script>
		$(function() {
			$(".fa-caret-square-right").click(function (e) {
				var id = $(this).attr("data-id");
				window.location = webContext + "/chart/get/" + id;
			});				
		});
	</script>
	
</mny:standardLayout>
