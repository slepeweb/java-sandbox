<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/searchBar.tag --></gen:debug>

<form id="search-bar" action="/searchresults" method="post" 
	enctype="application/x-www-form-urlencoded" accept-charset="utf-8">

		<input type="text" name="searchtext" value="${_params.searchText}"
			placeholder="Enter search terms" /><button 
				type="submit"><span><i class="fa fa-search"></i></span></button>
				
		<input type="hidden" name="page" value="1" />
</form>

<script>
	$(function() {
		$("#search-bar span").click(function() {
			$("#search-bar").submit();
		});
	});
</script>
