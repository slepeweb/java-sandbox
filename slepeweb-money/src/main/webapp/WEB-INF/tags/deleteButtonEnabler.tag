<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %>

<!-- deleteButtonEnabler.tag -->

<c:if test="${_formMode eq 'update'}">
	<c:if test="${not (_isAdmin or _numDeletableTransactions eq 0)}">
		<c:set var="disabler" value="" />
		<c:if test="${not (_isAdmin or _numDeletableTransactions eq 0)}"><c:set var="disabler">disabled="true"</c:set></c:if>
 		<input type="button" value="Delete ${entity}?" id="delete-button" ${disabler} /> 
 	</c:if>
</c:if>
