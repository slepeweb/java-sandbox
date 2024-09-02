<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>

	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>
		
</div>

<site:insertComponents site="${_item.site.shortname}" list="${_comp.components}" />
