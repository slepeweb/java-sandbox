<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- chartResults.tag -->

<c:if test="${not empty _chartSVG}">
	${_chartSVG}
</c:if>

<c:if test="${not empty _notes}">
	<div class="chart-notes">
		<h3>Notes</h3>
		<ul>
			<c:forEach items="${_notes}" var="_n">
				<li>${_n}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
	