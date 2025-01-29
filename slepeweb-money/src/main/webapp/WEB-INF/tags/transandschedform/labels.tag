<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entityName" required="true" rtexprvalue="true" %>

<!-- transandschedform/labels.tag -->

<c:set scope="request" var="_buttonLabel" value="Add ${entityName}" />
<c:set scope="request" var="_pageHeading" value="Add new ${entityName}" />
<c:if test="${_formMode eq 'update'}">
	<c:set scope="request" var="_buttonLabel" value="Update ${entityName}" />
	<c:set scope="request" var="_pageHeading" value="Update ${entityName}" />
</c:if>

	