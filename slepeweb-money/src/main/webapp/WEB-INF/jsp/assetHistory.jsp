<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<c:set var="_extraInPageCss" scope="request">
	#tabs {
		font-size: 1em;
	}
	
	#data-tab th {
		font-size: 1.2em;
	}
	
	.debit {
		color: red;
	}

	.totals {
		font-weight: bold;
		font-size: 1.2em;
	}
</c:set>

<mny:standardLayout>

	<h2 class="inline-block">Asset History <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<div id="tabs">
		<ul>
			<li><a href="#results-tab">Results</a></li>
			<li><a href="#data-tab">Data</a></li>
		</ul>
		<div id="results-tab">${_assetSVG}</div>
		<div id="data-tab">
			<c:if test="${not empty _assetSVG}">
				<c:set var="_balance" value="${0}" />
				
				<table>
					<tr>
						<c:forTokens items="Year End,Income,Expense,Growth,Balance" delims="," var="_label">
							<th>${_label}</th>
						</c:forTokens>
					</tr>
					
					<c:forEach items="${_data}" var="_assetData">
						<c:set var="_balance" value="${_balance + _assetData.growth}" />
						<tr>
							<th>${_assetData.year}</th>
							<td>${mon:formatPounds(_assetData.income)}</td>
							<td>${mon:formatPounds(_assetData.expense)}</td>
							<td <c:if test="${_assetData.growth lt 0}">class="debit"</c:if>>${mon:formatPounds(_assetData.growth)}</td>
							<td>${mon:formatPounds(_balance)}</td>
						</tr>
					</c:forEach>
										
					<tr class="totals">
						<td>Totals</td>
						<td>${mon:formatPounds(_totals.income)}</td>
						<td>${mon:formatPounds(_totals.expense)}</td>
						<td>${mon:formatPounds(_totals.growth)}</td>
						<td></td>
					</tr>
			</table>
			</c:if>
		</div>
	</div>

	<script>
		$(function() {
			$("#tabs").tabs({
				active: 0
			});
		});
	</script>
	
</mny:standardLayout>