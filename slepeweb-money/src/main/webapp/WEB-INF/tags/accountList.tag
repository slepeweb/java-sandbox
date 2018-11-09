<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="list" required="true" rtexprvalue="true" type="java.util.List" %>

<c:if test="${not empty list}">
	<h2>${heading}</h2>	
	<div>
		<table>			
			<c:forEach items="${list}" var="_a">
				<tr>
					<td class="name">${_a.name}</td>
					<td class="type"></td>
					<td class="balance amount"></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</c:if>