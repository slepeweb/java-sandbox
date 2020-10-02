<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="search-bar">
	<span>Search: </span>
	<div class="search-comps">
		<input type="text" name="searchtext" value="${_params.searchText}" placeholder="Enter search terms" />
		<button type="button"><span><i class="fa fa-search"></i></span></button>	
	</div>			
</div>

<script>
	$(function() {
		$("#search-bar button").click(function() {
			$.ajax(_cms.ctx + "/rest/search", {
				type: "POST",
				cache: false,
				data: {
					key: _cms.editingItemId,
					searchtext: $("#search-bar input[name=searchtext]").val(),
				},
				dataType: "html",
				success: function(html, status, z) {
					$("#searchresults").html(html);
					$("#searchresults .navigate").click(function(event){
						_cms.support.renderItemForms($(this).attr("href"));
						event.preventDefault();
						_cms.dialog.close(_cms.dialog.searchresults);
					});
					_cms.dialog.open(_cms.dialog.searchresults);
				},
				error: function(jqxhr, status, message) {
					console.log(message);
				},
			});
		});
	});
</script>
