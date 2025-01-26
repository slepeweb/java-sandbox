<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<c:set var="_extraJs" scope="request" value="search.js,datepicker.js,minorcats.js" />

<c:set var="_extraInPageCss" scope="request">
	#saved-search-identifier {
		width: 50%;
		display: inline;
	}
	
	#tabs {
		font-size: 1em;
	}
	
	#form-tab, #results-tab {
		padding: 0.5em;
	}
	
	.table-summary {
		font-weight: bold;
		font-size: 1.2em;
	}
	
	input[name="submit-option"] {
		border: 2px solid red;
		background-color: #d99;
	}
	
	.radio-option {
		margin-right: 3em;
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

<c:choose><c:when test="${_formMode eq 'adhoc'}">
	<c:set var="_pageHeading" value="Ad-hoc search" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save</c:set>
</c:when><c:when test="${_formMode eq 'update'}">
	<c:set var="_pageHeading" value="Search results" scope="request" />
	<c:set var="_formActionUrl" scope="request">/search/save/${_ss.id }</c:set>
</c:when></c:choose>

<mny:standardLayout>

	<h2 class="inline-block">${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<div id="tabs">
		<ul>
			<li><a href="#form-tab">Form</a></li>
			<li><a href="#results-tab">Results</a></li>
		</ul>
		<div id="form-tab"><mny:advancedSearchForm /></div>		
		<div id="results-tab"><mny:advancedSearchResults /></div>
	</div>

	<script>
		$(function() {	
			$("#tabs").tabs({
				active: ${mon:tertiaryOp(not empty _response, 1, 0)}
			});
		});
	</script>
		
</mny:standardLayout>