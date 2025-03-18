<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/custom.jsp --></gen:debug>

<%-- This tag is based on simple.jsp, but includes a 
			user-specified jsp specified in the component 'data' field--%>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.blurb}"><div>${_comp.blurb}</div></c:if>	
	<jsp:include page="/WEB-INF/jsp/sws/component/${_comp.jsp}.jsp" />
	
	<c:if test="${fn:length(_comp.components) > 0}">
		<site:insertComponents site="${_item.site.shortname}" list="${_comp.components}" /> 
	</c:if>
</div>
