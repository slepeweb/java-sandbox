<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<h2>
	<span class="top-pdf-title">
	<c:set var="first" value="${true}" />
	 	<c:forEach items="${toptitle}" var="str">
	 		<c:if test="${not first}">&#9830;</c:if> 
	 		${str}
			<c:set var="first" value="${false}" />
	 	</c:forEach>
 	</span>
 	
 	<br />
	<span>${bottomtitle}</span> 	
</h2>

<div>${site:parseXimg(_item.fields.bodytext, _ximgService, _passkey)}</div>

<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />

<hr />
