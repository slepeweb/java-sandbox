<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>

	<mny:pageHeading heading="Categories">
		<a href="add" title="Create a new category">New category</a>
	</mny:pageHeading>
	
	<p><strong>Total no. of categories = ${_count}</strong></p>
	
	<c:choose><c:when test="${not empty _categories}">
	
		<div id="accordion">		
			<c:forEach items="${_categories}" var="_m">
				<h3 title="Display sub-categories of ${_m.name}">${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_c">
							<tr>
								<td class="name"><a href="${_ctxPath}/category/form/${_c.id}"
									title="Update details of this sub-category">${_c.minor}</a></td>
								<td>${_c.type}</td>
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
