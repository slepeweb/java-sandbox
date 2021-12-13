<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<c:set var="_extraCss" scope="request">
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

<c:set var="_formActionUrl" scope="request">/chart/post/${_ss.id }</c:set>

<mny:standardLayout>

	<c:set var="_help" value="" />
	<c:if test="${not empty _ss.description}">
		<c:set var="_help"><span>&nbsp;<i class="far fa-question-circle" title="${_ss.description}"></i></span></c:set>
	</c:if>
	
	<h2 class="inline-block">Chart results ${_help} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
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