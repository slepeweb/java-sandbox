<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="search-bar">
	<span>Search: </span>
	<div class="search-comps">
		<input type="text" name="searchtext" value="${_params.searchText}" placeholder="Enter search terms" />
		<button type="button"><span><i class="fa fa-search"></i></span></button>	
	</div>			
		
	<div id="trash-item-flag" title="Flag item for deletion">
		<i class="far fa-trash-alt trash-item-flag <c:if test='${_flagged4Trash}'>flagged</c:if>"></i>
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
					$("#searchresults .navigate").click(function(event) {
						var key = $(this).attr("href");
						_cms.leftnav.activateKey(key);
						_cms.support.renderItemForms(key);
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
