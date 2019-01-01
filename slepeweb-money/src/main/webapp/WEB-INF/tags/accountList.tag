<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="list" required="true" rtexprvalue="true" type="java.util.List" %>

<c:if test="${not empty list}">
	<h2>${heading}</h2>	
	<div>
		<table>			
			<c:forEach items="${list}" var="_a">
				<tr>
					<td class="name"><a href="${_ctxPath}/account/form/${_a.id}">${_a.name}</a></td>
					<td class="type">${_a.type}</td>
					<td class="menu-icon" data-id="${_a.id}"><i class="fas fa-bars"></i></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</c:if>
