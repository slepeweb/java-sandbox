<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:choose><c:when test="${not empty noCategoriesSpecified}">
	<p>No categories specified - please <a href="${_ctxPath}/chart/by/categories${queryString}">try again</a>.</p>
</c:when><c:otherwise>
	<c:if test="${not empty _chartSVG}">
		<table>
			<c:set var="_year" value="${_chartProps.fromYear}" />
			<c:set var="_numGroups" value="${fn:length(_chartDataMap)}" />
			
			<c:forEach items="${_chartLabels}" var="_label">
				<tr>
					<th>${_label}</th>
					<c:forEach items="${_years}" var="_year">
						<c:set var="_list" value="${_chartDataMap[_label]}" />
						<td>${mon:tertiaryOp(not empty _list, mon:formatPounds(_list.data[_year]), "")}</td>
					</c:forEach>
					<td>${mon:formatPounds(_chartDataMap[_label].total)}</td>
				</tr>
			</c:forEach>
			
			<tr>
				<th>Year</th>
				<c:forEach items="${_years}" var="_year">
					<th>${_year}</th>
				</c:forEach>
				<th>Totals</th>
			</tr>
		</table>
	</c:if>
</c:otherwise></c:choose>
	