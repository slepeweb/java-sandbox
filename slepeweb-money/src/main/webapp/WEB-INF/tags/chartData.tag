<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- chartData.tag -->

<c:choose><c:when test="${not empty noCategoriesSpecified}">
	<p>No categories specified - please <a href="${_ctxPath}/chart/by/categories${queryString}">try again</a>.</p>
</c:when><c:otherwise>
	<c:if test="${not empty _chartSVG}">
		<table>
			<c:set var="_year" value="${_chartProps.fromYear}" />
			<c:set var="_numGroups" value="${fn:length(_chartDataMap)}" />
			
			<tr>
				<th></th>
				<c:forEach items="${_chartLabels}" var="_label">
					<th>${_label}</th>
				</c:forEach>
			</tr>
			
			<c:forEach items="${_years}" var="_year">
				<tr>
					<th>${_year}</th>
					<c:forEach items="${_chartLabels}" var="_label">
						<c:set var="_list" value="${_chartDataMap[_label]}" />
						<td>${mon:tertiaryOp(not empty _list, mon:formatPounds(_list.data[_year]), "")}</td>
					</c:forEach>
				</tr>
			</c:forEach>
			
			<tr>
				<th>Totals</th>
				<c:forEach items="${_chartLabels}" var="_label">
					<td>${mon:formatPounds(_chartDataMap[_label].total)}</td>
				</c:forEach>
			</tr>
			
		</table>
	</c:if>
</c:otherwise></c:choose>
	