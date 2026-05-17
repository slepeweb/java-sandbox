<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request" value="searchAndChartLists.js" />

<mny:standardLayout pageId="chartList">

	<mny:pageHeading heading="Chart list">
		<a href="${_ctxPath}/chart/add" title="Define and save new chart critera">New chart</a>
	</mny:pageHeading>

	<c:choose><c:when test="${not empty _charts}">
		<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Execute</th>
					<th>Delete?</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${_charts}" var="_ch">
					<tr>
						<td><a href="${_ctxPath}/chart/edit/${_ch.id}">${_ch.name}</a></td>
						<td>${_ch.description}</td>
						<td><i class="far fa-caret-square-right" title="Generate the chart"
							data-id="${_ch.id}"></i></td>
						<td><i class="fa fa-trash" title="Delete this chart definition"
							data-id="${_ch.id}"></i></td>
					</tr>
				</c:forEach>
			</tbody>			
		</table>					
	</c:when><c:otherwise>
		<p><strong>No saved charts</strong></p>
	</c:otherwise></c:choose>
	
	<mny:listEntryDeletionDialog entity="chart" />

</mny:standardLayout>
	