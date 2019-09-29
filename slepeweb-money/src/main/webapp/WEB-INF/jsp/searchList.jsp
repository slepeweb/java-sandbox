<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<div class="right"><a href="${_ctxPath}/search/create">New search</a></div>
	
	<h2>Saved searches <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<c:choose><c:when test="${not empty _searches}">
		<table>
			<tr>
				<th>Date created</th>
				<th>Name</th>
				<th>Execute</th>
			</tr>
			<c:forEach items="${_searches}" var="_ss">
				<tr>
					<td><a href="${_ctxPath}/search/edit/${_ss.id}">${_ss.savedWithMinutes}</a></td>
					<td>${_ss.name}</td>
					<td><i class="far fa-caret-square-right" title="Execute this search" data-id="${_ss.id}"></i></td>
				</tr>
			</c:forEach>			
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
