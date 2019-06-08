<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<mny:standardLayout>

	<h2>Advanced search	<c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<mny:advancedSearchForm />
	<mny:advancedSearchResults />

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

		});
	</script>
	
</mny:standardLayout>