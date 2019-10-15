<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:choose><c:when test="${_formMode eq 'create'}">
	<c:set var="_pageHeading" value="Create new search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save</c:set>
</c:when><c:when test="${_formMode eq 'update'}">
	<c:set var="_pageHeading" value="Update search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/update/${_ss.id }</c:set>
</c:when></c:choose>

<c:set var="_extraCss" scope="request">
	.ui-autocomplete {
		font-size: 1.0em;
	}
	
	input[name="submit-option"] {
		border: 2px solid red;
		background-color: #d99;
	}
</c:set>

<mny:standardLayout>

	<h2>${_pageHeading}</h2>
	<mny:advancedSearchForm />		
	<mny:entityDeletionDialog entity="search" mode="${_formMode}" id="${_ss.id}"/>

</mny:standardLayout>