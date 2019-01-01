<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_flashMessage" value="" scope="request" />
<c:set var="_flashType" value="" scope="request" />
<c:if test="${not empty param.flash}">
	<c:set var="_flashMessage" value="${fn:substringAfter(param.flash, '|')}" scope="request" />
	<c:set var="_flashType" value="${fn:substringBefore(param.flash, '|')}" scope="request" />
</c:if>
	