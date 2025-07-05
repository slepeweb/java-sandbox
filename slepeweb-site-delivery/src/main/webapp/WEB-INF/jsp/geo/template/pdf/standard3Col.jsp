<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<h2>
	<c:set var="first" value="${true}" />
	
 	<c:forEach items="${_page.header.breadcrumbs}" var="i">
 		<c:if test="${i.href ne '/'}">
	 		<c:if test="${not first}">&diams;</c:if> 
	 		${i.title}
			<c:set var="first" value="${false}" />
		</c:if>
 	</c:forEach>
</h2>

<div>${site:parseXimg(_item.fields.bodytext, _ximgService, _passkey)}</div>

<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />
