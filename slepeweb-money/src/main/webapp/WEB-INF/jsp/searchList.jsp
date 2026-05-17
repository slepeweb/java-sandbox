<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraJs" scope="request" value="searchAndChartLists.js" />
	
<mny:standardLayout pageId="searchList">

	<mny:pageHeading heading="Saved searches">
		<ul>
			<li><a href="${_ctxPath}/search/add" title="Define and save new search critera">New search</a></li>
			<li><a href="${_ctxPath}/search/adhoc" title="Define an ad-hoc search that won't get saved in the database">Ad-hoc</a><li>
		</ul>
	</mny:pageHeading>
	
	<c:choose><c:when test="${not empty _searches}">
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
				<c:forEach items="${_searches}" var="_ss">
					<tr>
						<td><a href="${_ctxPath}/search/edit/${_ss.id}">${_ss.name}</a></td>
						<td>${_ss.description}</td>
						<td><i class="far fa-caret-square-right" title="Execute this search" data-id="${_ss.id}"></i></td>
						<td><i class="fa fa-trash" title="Delete this chart definition" data-id="${_ss.id}"></i></td>
					</tr>
				</c:forEach>	
			</tbody>		
		</table>					
	</c:when><c:otherwise>
		<p><strong>No saved searches</strong></p>
	</c:otherwise></c:choose>
	
	<mny:listEntryDeletionDialog entity="search" />
	
</mny:standardLayout>
