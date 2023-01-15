<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<h3>There are ${fn:length(_flaggedItems)} flagged items.</h3>

<c:if test="${fn:length(_flaggedItems) > 0}">
	<ul>
		<c:forEach items="${_flaggedItems}" var="_gist">
			<li>
				<div class="cms-icon cms-icon-${fn:toLowerCase(_gist.type)}"></div>
				<a href="#" class="navigate" data-id="${_gist.itemId}">${_gist.name} (${_gist.path})</a>
			</li>
		</c:forEach>
	</ul>
</c:if>

<script>
	$(function() {
		$('#flagged-items-dialog a.navigate').click(function(e) {
			e.preventDefault();
			let nodeKey = $(this).attr('data-id');
			_cms.leftnav.navigate(nodeKey);
		});
	});
</script>