<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %>

<c:set var="_buttonLabel" value="Add ${entity}" scope="request" />
<c:set var="_pageHeading" value="Add new ${entity}" scope="request" />

<c:if test="${_formMode eq 'update'}">
	<c:set var="_buttonLabel" value="Update ${entity}" scope="request" />
	<c:set var="_pageHeading" value="Update ${entity}" scope="request" />
</c:if>
	