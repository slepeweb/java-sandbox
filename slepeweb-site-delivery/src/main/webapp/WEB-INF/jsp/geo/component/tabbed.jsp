<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="componentId" value="component-${_comp.id}" />
<c:set var="clazz" value="tabbed" />
<c:if test="${not empty _comp.cssClass}"><c:set var="clazz" value="${clazz} ${_comp.cssClass}" /></c:if>

<div id="${componentId}" class="${clazz}">

	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>	
	
	<ul>
		<c:forEach items="${_comp.components}" var="subComp">
			<li><a href="#${site:compress(subComp.identifier)}-tab">${subComp.identifier}</a></li>
		</c:forEach>
	</ul>
	
	<c:forEach items="${_comp.components}" var="subComp">
		<div id="${site:compress(subComp.identifier)}-tab">
			<site:insertComponent site="${_item.site.shortname}" component="${subComp}" />
		</div>
	</c:forEach>
	
</div>

<script>
if ($) {
	$(function() {
		$("#${componentId}").tabs();
	})
}
</script>
