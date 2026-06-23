<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- chartResults.tag -->

<c:if test="${not empty _chartSVG}">
	${_chartSVG}
</c:if>

<c:if test="${not empty _chart.notes}">
	<h3>Notes</h3>
	<ul>
		<c:forEach items="${_chart.notesAsList}" var="_line">
			<li>${_line}</li>
		</c:forEach>
	</ul>
</c:if>
	