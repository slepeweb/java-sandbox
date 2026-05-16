<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_flashMessage" value="" scope="request" />
<c:set var="_flashType" value="" scope="request" />

<%-- _flasher would be set by the controller --%>
<c:if test="${empty _flasher}"><c:set var="_flasher" value="${param.flash}" /></c:if>

<c:if test="${not empty _flasher}">
	<c:set var="_flashMessage" value="${fn:substringAfter(_flasher, '|')}" scope="request" />
	<c:set var="_flashType" value="${fn:substringBefore(_flasher, '|')}" scope="request" />
</c:if>

