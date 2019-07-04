<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<c:set var="_extraCss" scope="request">
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
</c:set>

<c:set var="_formActionUrl" scope="request">/search/post/${_ss.id}</c:set>

<mny:standardLayout>

	<h2 class="inline-block">Advanced search <c:if test="${not empty param.flash}"><span 
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