<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Categories</h2>			
	<c:choose><c:when test="${not empty _categories}">
	
		<div id="accordion">		
			<c:forEach items="${_categories}" var="_m">
				<h3><i class="fas fa-bars category-menu"></i>&nbsp;&nbsp;${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_c">
							<tr>
								<td data-id="${_c.id}"><i class="fas fa-bars category-menu"></i>&nbsp;&nbsp;${_c.minor}</td>
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

<div id="category-menu-dialog">
	<ul>
		<li>Edit</li>
		<li>Delete</li>
		<li class="category-menu-find">Find transactions</li>
		<li class="category-menu-close">Close menu</li>
	</ul>
</div>