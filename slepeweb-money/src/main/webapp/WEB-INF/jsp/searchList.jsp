<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<div class="right">
		<a href="${_ctxPath}/search/create" title="Define and save new search critera">New search</a><br />
		<a href="${_ctxPath}/search/adhoc" title="Define an ad-hoc search that won't get saved in the database">Ad-hoc</a>
	</div>
	
	<h2>Saved searches <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<c:choose><c:when test="${not empty _searches}">
		<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Date created</th>
					<th>Execute</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${_searches}" var="_ss">
					<tr>
						<td><a href="${_ctxPath}/search/edit/${_ss.id}" title="${mon:renderEither(_ss.description, 'Click to update parameters for this search')}">${_ss.name}</a></td>
						<td>${_ss.savedWithMinutes}</td>
						<td><i class="far fa-caret-square-right" title="Execute this search" data-id="${_ss.id}"></i></td>
					</tr>
				</c:forEach>	
			</tbody>		
		</table>					
	</c:when><c:otherwise>
		<p><strong>No saved searches</strong></p>
	</c:otherwise></c:choose>
	
	<script>
		$(function() {
			$(".fa-caret-square-right").click(function (e) {
				var params = $(this).attr("data-params");
				var id = $(this).attr("data-id");
				window.location = webContext + "/search/get/" + id;
			});	
		});
	</script>
	
</mny:standardLayout>
