<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<%-- <c:set var="_extraJs" scope="request">/resources/geo/js/homepage.js</c:set> --%>

<geo:pageLayout type="std">
	<gen:debug><!-- jsp/geo/searchresults.jsp --></gen:debug>
		
	<div class="main searchresults">
		<h2>${_item.fields.title}</h2>
		<p>${_item.fields.bodytext}</p>
		
		<c:choose><c:when test="${fn:length(_searchResponse.results) gt 0}">
			<ul>
				<c:forEach items="${_searchResponse.results}" var="_result">
					<li>
						<c:set var="_teaser" value="n/a" />
						<c:if test="${not empty _result.teaser}">
							<c:set var="_teaser" value="${_result.teaser}" />
						</c:if>
						<a href="${_result.path}" title="${_teaser}">${_result.title}</a>
					</li>
				</c:forEach>
			</ul>
		</c:when><c:otherwise>
			<h3>No results found</h3>
		</c:otherwise></c:choose>
	</div>
</geo:pageLayout>