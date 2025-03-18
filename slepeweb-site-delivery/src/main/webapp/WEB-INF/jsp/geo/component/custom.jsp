<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/custom.jsp --></gen:debug>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>	
	
	<c:if test="${fn:length(_comp.components) > 0}">
		<site:insertComponents site="${_item.site.shortname}" list="${_comp.components}" /> 
	</c:if>
</div>

<script>
${_comp.js}
</script>
