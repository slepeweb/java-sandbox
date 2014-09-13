<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<%-- <div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>> --%>

	<script>
		$(function() {
			$("#tabs").tabs(
			//{active: 0}
			);
		});
	</script>

	<div id="tabs">
		<ul>
			<c:forEach items="${_comp.components}" var="component" varStatus="status">
				<li><a href="#tabs-${status.count}">${component.heading}</a></li>
			</c:forEach>
		</ul>

		<c:forEach items="${_comp.components}" var="component" varStatus="status">
			<div id="tabs-${status.count}" class="compact">
				<gen:insertComponent site="${_site.shortname}" component="${component}" />
			</div>
		</c:forEach>
	</div>
	
<!-- </div> -->
