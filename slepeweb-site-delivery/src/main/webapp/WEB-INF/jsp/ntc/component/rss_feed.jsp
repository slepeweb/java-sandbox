<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/ntc/component/rss_feed.jsp --></gen:debug>

<script>
	$(document).ready(function(){
		$(".group2").colorbox({
			rel:'group2', 
			transition:"none", 
			current:'Story {current} of {total}',
			title: function() {
				return $(this).text();
			}});
		
		$(".iframe").colorbox({
			iframe:true, 
			opacity:0.5, 
			closeButton:true, 
			width:"90%", 
			height:"90%"});
	});
</script>

<c:set var="_xclass" value="" />
<c:if test="${_comp.view eq 'rightside'}"><c:set var="_xclass" value="sidebar" /></c:if>

<div class="raised-box ${_xclass}">
	<h2>${_comp.heading}</h2>
	<div>${_comp.blurb}</div>
	<ul class="link-list">
		<c:forEach items="${_comp.targets}" var="link" end="3">
			<li class="compact"><a href="${link.href}" class="iframe group2">${link.title}</a></li>
		</c:forEach>
	</ul>
</div>
