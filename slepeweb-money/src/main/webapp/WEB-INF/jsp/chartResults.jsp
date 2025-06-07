<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraJs" scope="request" value="chart.js,search.js,minorcats.js" />

<!-- chartResults.jsp -->

<mny:flash />

<c:set var="_extraInPageCss" scope="request">
	#tabs {
		font-size: 1em;
	}
	
	#form-tab, #results-tab {
		padding: 0.5em;
	}
	
	#data-tab th {
		font-size: 1.2em;
	}

	input[type="submit"] {
		margin-top: 1.0em;
	}
</c:set>

<c:set var="_formActionUrl" scope="request">/chart/save/${_ss.id }</c:set>

<mny:standardLayout>

	<c:set var="_help" value="" />
	<c:if test="${not empty _ss.description}">
		<c:set var="_help"><span>&nbsp;<i class="far fa-question-circle" title="${_ss.description}"></i></span></c:set>
	</c:if>
	
	<mny:pageHeading heading="Chart results ${_help}">
		<a href="${_ctxPath}/chart/list" title="List saved charts">List charts</a>
	</mny:pageHeading>
			
	<div id="tabs">
		<ul>
			<li><a href="#form-tab">Form</a></li>
			<li><a href="#results-tab">Results</a></li>
			<li><a href="#data-tab">Data</a></li>
		</ul>
		<div id="form-tab"><mny:chartForm /></div>		
		<div id="results-tab"><mny:chartResults /></div>
		<div id="data-tab"><mny:chartData /></div>
	</div>

	<script>
		$(function() {
			$("#tabs").tabs({
				active: ${mon:tertiaryOp(not empty _chartSVG, 1, 0)}
			});
		});
	</script>
	
</mny:standardLayout>