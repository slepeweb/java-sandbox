<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/component/accordion.jsp --></gen:debug>

<c:set var="componentId" value="component-${_comp.enumerator}" />
<c:set var="clazz" value="accordion" />
<c:if test="${not empty _comp.cssClass}"><c:set var="clazz" value="${clazz} ${_comp.cssClass}" /></c:if>

<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>	

<div id="${componentId}" class="${clazz}">

	<c:forEach items="${_comp.components}" var="subComp">
		<h3>${subComp.identifier}</h3>
		<div>
			<site:insertComponent site="${_item.site.shortname}" component="${subComp}" view="${_item.requestPack.view}" />
		</div>
	</c:forEach>	
</div>

<script>
if ($) {
	$(function() {
		$("#${componentId}").accordion({active: false, collapsible: true, heightStyle: 'content'});
	})
}
</script>
