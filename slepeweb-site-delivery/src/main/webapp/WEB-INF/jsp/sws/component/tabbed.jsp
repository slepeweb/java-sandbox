<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/tabbed.jsp --></gen:debug>

<script>
	$(function() {
		$("#tabs").tabs(
		//{active: 0}
		);
	});
</script>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
	<div id="tabs">
		<ul>
			<c:forEach items="${_comp.components}" var="component" varStatus="status">
				<li><a href="#tabs-${status.count}">${component.identifier}</a></li>
			</c:forEach>
		</ul>
	
		<c:forEach items="${_comp.components}" var="component" varStatus="status">
			<div id="tabs-${status.count}" class="compact">
				<site:insertComponent site="${_item.site.shortname}" component="${component}" /> 
			</div>
		</c:forEach>
	</div>
</div>