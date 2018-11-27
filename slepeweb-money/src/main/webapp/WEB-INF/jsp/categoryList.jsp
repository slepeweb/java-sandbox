<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Categories</h2>			
	<p><strong>Total no. of categories = ${_count}</strong></p>
	
	<c:choose><c:when test="${not empty _categories}">
	
		<div id="accordion">		
			<c:forEach items="${_categories}" var="_m">
				<h3>${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_c">
							<tr>
								<td class="name">${_c.minor}</td>
								<td data-id="${_c.id}" class="menu-icon"><i class="fas fa-bars"></i></td>
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

<mny:menuActionDialog target="/money/transaction/list/by/category/" />
