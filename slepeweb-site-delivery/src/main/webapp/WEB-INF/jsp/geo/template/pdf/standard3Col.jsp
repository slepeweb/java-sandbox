<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<h2>
<<<<<<< Upstream, based on branch 'master' of https://github.com/slepeweb/java-sandbox.git
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
=======
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
>>>>>>> 5c146fe cms-d: pdf gen, stage 1
