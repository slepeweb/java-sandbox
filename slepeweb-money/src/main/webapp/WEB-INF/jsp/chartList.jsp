<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<div class="right"><a href="${_ctxPath}/chart/create">New chart</a></div>
	
	<h2>Chart list <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
		
	<c:choose><c:when test="${not empty _charts}">
		<table>
			<tr>
				<th>Date created</th>
				<th>Name</th>
				<th>Execute</th>
			</tr>
			<c:forEach items="${_charts}" var="_ss">
				<tr>
					<td><a href="${_ctxPath}/chart/edit/${_ss.id}">${_ss.saved}</a></td>
					<td>${_ss.name}</td>
					<td><i class="far fa-caret-square-right" title="Execute this search"
						data-params="${mon:encodeUrl(_ss.json)}" data-id="${_ss.id}"></i></td>
				</tr>
			</c:forEach>			
		</table>					
	</c:when><c:otherwise>
		<p><strong>No saved charts</strong></p>
	</c:otherwise></c:choose>
	
	<script>
		$(function() {
			$(".fa-caret-square-right").click(function (e) {
				var params = $(this).attr("data-params");
				var id = $(this).attr("data-id");
				window.location = webContext + "/chart/get/" + id + "?json=" + params;
			});				
		});
	</script>
	
</mny:standardLayout>
