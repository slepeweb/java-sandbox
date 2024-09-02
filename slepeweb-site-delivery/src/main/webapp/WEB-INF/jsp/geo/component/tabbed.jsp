<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:set var="componentId" value="component-${_comp.id}" />
<div id="${componentId}" <c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>

	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>	
	
	<ul>
		<c:forEach items="${_comp.components}" var="subComp">
			<li><a href="#${subComp.identifier}-tab">${subComp.identifier}</a></li>
		</c:forEach>
	</ul>
	
	<c:forEach items="${_comp.components}" var="subComp">
		<div id="${subComp.identifier}-tab">
			<site:insertComponent site="${_item.site.shortname}" component="${subComp}" />
		</div>
	</c:forEach>
	
</div>

<script>
$(function() {
	$("#${componentId}").tabs();
});
</script>
