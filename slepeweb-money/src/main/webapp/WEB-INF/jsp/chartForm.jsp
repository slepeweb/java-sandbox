<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.jsp -->

<c:set var="_extraJs" scope="request" value="chart.js,search.js,minorcats.js" />
<c:set var="_extraCss" scope="request" value="search.css "/>

<c:choose><c:when test="${_formMode eq 'add'}">
	<c:set var="_pageHeading" value="Create new chart" scope="request" />
	<c:set var="_formActionUrl" scope="request">/chart/save</c:set>
</c:when><c:when test="${_formMode eq 'update'}">
	<c:set var="_pageHeading" value="Update chart" scope="request" />
	<c:set var="_formActionUrl" scope="request">/chart/save/${_ss.id }</c:set>
</c:when></c:choose>

<mny:standardLayout>
	
	<mny:pageHeading heading="${_pageHeading}">
		<mny:standardFormMenu entity="chart" />
	</mny:pageHeading>

	<details style="margin-bottom: 2em; cursor: pointer;">
		<summary><i class="fa-solid fa-info fa-2x"></i></summary>
		<ul>
			<li>Charts aggregate transaction year by year, and present the totals in a bar chart. </li>
			<li>Unless transactions of interest are identified as 'Transfers', groups of categories
					can be defined, and each group will be represented in the bar chart in its own colour.</li>
			<li>Category groups do not make sense when dealing with Transfers, and so are not available in that case.</li>
		</ul>
	</details>
	
	<mny:chartForm />	

	<mny:entityDeletionDialog entity="chart" mode="${_formMode}" id="${_ss.id}"/>

</mny:standardLayout>