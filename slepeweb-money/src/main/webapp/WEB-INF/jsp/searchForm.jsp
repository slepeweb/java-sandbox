<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- searchForm.jsp -->

<c:choose><c:when test="${_formMode eq 'adhoc'}">
	<c:set var="_pageHeading" value="Ad-hoc search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save</c:set>
</c:when><c:when test="${_formMode eq 'add'}">
	<c:set var="_pageHeading" value="Create new search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save</c:set>
</c:when><c:when test="${_formMode eq 'update'}">
	<c:set var="_pageHeading" value="Update search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save/${_ss.id }</c:set>
</c:when></c:choose>

<c:set var="_extraJs" scope="request" value="search.js,datepicker.js,minorcats.js" />

<c:set var="_extraInPageCss" scope="request">
	.ui-autocomplete {
		font-size: 1.0em;
	}
	
	input[name="submit-option"] {
		border: 2px solid red;
		background-color: #d99;
	}
	
	input.datepicker, input.amount {
		width: 30%;
		display: inline;
	}
	
	select.amount {
		width: 20%;
		display: inline;
	}
</c:set>

<c:set var="_extraInPageJs" scope="request">
	_money.search.formMode = '${_formMode}';
</c:set>

<mny:standardLayout>

	<mny:pageHeading heading="${_pageHeading}">
		<mny:standardFormMenu entity="search" many="searches" />
	</mny:pageHeading>

	<mny:advancedSearchForm />	
	
	<mny:entityDeletionDialog entity="search" mode="${_formMode}" id="${_ss.id}"/>

</mny:standardLayout>