<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>

	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>
		
	<c:if test="${not empty _comp.targets and fn:length(_comp.targets gt 0)}">
		<ul>
		<c:forEach items="${_comp.targets}" var="link">
			<a href="${link.href}">${link.title}</a>
		</c:forEach>
		</ul>
	</c:if>
	
</div>
