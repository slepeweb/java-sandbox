<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div class="main pdf">
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
	
	<c:set var="_bodytext">
		<div>${_item.fields.bodytext}</div>
		<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />
	</c:set>
	
	<c:set var="_updatedBody" value="${site:parseXimg(_bodytext, _ximgService, _passkey)}" />
	<div>${site:parseXcomp(_updatedBody, _xcompService)}</div> 

</div>
<hr />
