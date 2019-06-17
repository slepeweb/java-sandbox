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

<mny:standardLayout>

	<h2 class="inline-block">Advanced search <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<div class="right"><a href="${_ctxPath}/search/save/list">Saved searches</a></div>

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
			$(".pager a").click(function (e) {
				var action = $(this).attr("href");
				var form = $("#advanced-search-form");
				form.attr("action", action);
				form.submit();
				e.preventDefault();
				e.stopPropagation();
			});
			
			$("#save-search-button").click(function (e) {
				var form = $("#advanced-search-form");
				form.attr("action", webContext + "/search/save/advanced");
				form.submit();
			});

			$("#tabs").tabs({
				active: ${mon:tertiaryOp(not empty _response, 1, 0)}
			});
		});
	</script>
	
</mny:standardLayout>