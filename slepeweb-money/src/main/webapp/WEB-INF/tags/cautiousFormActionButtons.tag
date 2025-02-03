<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="label" required="true" rtexprvalue="true" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %>

<!-- cautiousFormActionButtons.tag -->

<div class="cautious-form-buttons">
	<div class="left">
		<input type="submit" value="${label}" /> 
	</div>
	
	<div class="right">
		<c:if test="${_formMode eq 'update'}">
			<c:if test="${not (_isAdmin or _numDeletableTransactions eq 0)}">
				<c:set var="disabler" value="" />
				<c:if test="${not (_isAdmin or _numDeletableTransactions eq 0)}"><c:set var="disabler">disabled="true"</c:set></c:if>
		 		<input type="button" value="Delete ${entity}?" id="delete-button" ${disabler} /> 
		 	</c:if>
		</c:if>
	</div>
</div>
