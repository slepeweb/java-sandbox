<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2 class="inline-block">Categories <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>			
	
	<div class="right"><a href="add">New category</a></div>
	
	<p><strong>Total no. of categories = ${_count}</strong></p>
	
	<c:choose><c:when test="${not empty _categories}">
	
		<div id="accordion">		
			<c:forEach items="${_categories}" var="_m">
				<h3>${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_c">
							<tr>
								<td class="name"><a href="form/${_c.id}">${_c.minor}</a></td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:forEach>			
		</div>
					
	</c:when><c:otherwise>
		<p><strong>No categories defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>
