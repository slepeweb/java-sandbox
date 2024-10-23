<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>	

<div id="${_comp.data}" <c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
	<c:forEach items="${_comp.components}" var="subComp">
		<h3>${subComp.identifier}</h3>
		<div>
			<site:insertComponent site="${_item.site.shortname}" component="${subComp}" />
		</div>
	</c:forEach>	
</div>

<script>
${_comp.js}
</script>
