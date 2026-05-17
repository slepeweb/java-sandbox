<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="id" required="true" rtexprvalue="true" %><%@ 
	attribute name="level" required="false" rtexprvalue="true" %>

<!-- entityDeletionDialog.tag -->
<%-- 
This dialog appears on ALL form pages. It
requires money.js to have been loaded on the page.
--%>

<script>
	_money.param.entityType = '${entity}';
	_money.param.entityId = '${id}';
	_money.param.numDeletables = '${_numDeletableTransactions}'
</script>

<div id="delete-dialog" title="Delete ${entity}">
	Please NOTE that ALL deletions are FINAL, and CANNOT be undone.<br />
	<c:choose><c:when test="${entity eq 'transaction'}">
		Are you sure you wish to delete this transaction?
	</c:when><c:when test="${entity eq 'chart'}">
		Are you sure you wish to delete this chart?
	</c:when><c:when test="${entity eq 'search'}">
		Are you sure you wish to delete this search?
	</c:when><c:when test="${entity eq 'schedule'}">
		Are you sure you wish to delete this schedule?
	</c:when><c:otherwise>
		<c:if test="${_numDeletableTransactions gt 0}">
			Deleting this ${level}${entity} will also delete ${_numDeletableTransactions} 
			corresponding transactions.
		</c:if>
		Are you sure you wish to proceed?
	</c:otherwise></c:choose>
</div>
