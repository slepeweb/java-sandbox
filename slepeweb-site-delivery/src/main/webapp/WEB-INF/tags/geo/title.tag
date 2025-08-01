<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/geo/title.tag --></gen:debug>

<c:choose><c:when test="${empty _item.requestPack.view or _item.requestPack.view ne 'pdf'}">
	<h2 id="page-title">${_item.fields.title}</h2>
</c:when><c:otherwise>

	<h2>
		<span class="top-pdf-title">
		<c:set var="first" value="${true}" />
		 	<c:forEach items="${_toptitle}" var="str">
		 		<c:if test="${not first}">&#9830;</c:if> 
		 		${str}
				<c:set var="first" value="${false}" />
		 	</c:forEach>
	 	</span>
	 	
	 	<br />
		<span>${_bottomtitle}</span> 	
	</h2>

</c:otherwise></c:choose>