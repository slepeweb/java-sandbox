<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="many" required="false" rtexprvalue="true" %>

<!-- standardFormMenu.tag -->

<c:set var="many" value="${empty many ? entity : many}" />

<ul>
	<c:if test="${_formMode eq 'update'}">
		<li><a href="${_ctxPath}/${entity}/add" title="Create a new ${entity}">New ${entity}</a></li>
	</c:if>
	<c:if test="${_formMode eq 'add' or _formMode eq 'adhoc' or _formMode eq 'update'}">
		<li><a href="${_ctxPath}/${entity}/list" title="List existing ${entity}s">List ${many}</a></li>
	</c:if>
</ul>
	