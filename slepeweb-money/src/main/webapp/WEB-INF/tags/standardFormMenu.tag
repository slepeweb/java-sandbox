<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="many" required="false" rtexprvalue="true" %>

<!-- standardFormMenu.tag -->

<%-- entity: [${entity}], many: [${many}], length: ${fn:length(many)} --%>
<c:set var="several">${entity}s</c:set>
<c:if test="${fn:length(many) gt 1}"><c:set var="several" value="${many}" /></c:if>

<ul>
	<c:if test="${_formMode eq 'update'}">
		<li><a href="${_ctxPath}/${entity}/add" title="Create a new ${entity}">New ${entity}</a></li>
	</c:if>
	<c:if test="${_formMode eq 'add' or _formMode eq 'adhoc' or _formMode eq 'update'}">
		<li><a href="${_ctxPath}/${entity}/list" title="List existing ${several}">List ${several}</a></li>
	</c:if>
</ul>
	