<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="list" required="true" rtexprvalue="true" type="java.util.List" %>

<!-- accountList.tag -->

<c:if test="${not empty list}">
	<h2>${heading}</h2>	
	<div>
		<table>			
			<c:forEach items="${list}" var="_a">
				<tr>
					<td class="name"><a href="${_ctxPath}/account/form/${_a.id}" 
						title="Update details of this account">${_a.name}</a></td>
					<td class="type">${_a.type}</td>
					<td>${_a.note}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</c:if>
