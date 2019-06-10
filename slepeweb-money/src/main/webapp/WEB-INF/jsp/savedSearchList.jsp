<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<h2>Saved searches <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<c:choose><c:when test="${not empty _types}">
		<div id="accordion-ss">
			<c:forEach items="${_types}" var="_type">
				<c:set var="_list" value="${_map[_type]}" />
				<h2>${_type}</h2>
				<div>
					<table>
						<c:forEach items="${_list}" var="_ss">
							<tr>
								<td>${_ss.saved}</td>
								<td>${_ss.name}</td>
								<td><i class="far fa-caret-square-right" title="Execute this search"
									data-params="${mon:encodeUrl(_ss.json)}" data-type="${_ss.type}"></i></td>
							</tr>
						</c:forEach>			
					</table>
				</div>
			</c:forEach>
		</div>
					
	</c:when><c:otherwise>
		<p><strong>No saved searches</strong></p>
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
