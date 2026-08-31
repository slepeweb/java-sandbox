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
		<div id="results-tab">
			${_assetSVG}
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
		</div>
		
		<div id="data-tab">
			<c:if test="${not empty _assetSVG}">
				<c:set var="_balance" value="${0}" />
				
				<table>
					<tr>
						<c:forTokens items="Year End,Income,Expense,Balance" delims="," var="_label">
							<th>${_label}</th>
						</c:forTokens>
					</tr>
					
					<c:forEach items="${_history.list}" var="_assetData">
						<tr>
							<th>${_assetData.year}</th>
							<td>${mon:formatPounds(_assetData.income)}</td>
							<td>${mon:formatPounds(_assetData.expense)}</td>
							<td>${mon:formatPounds(_assetData.balance)}</td>
						</tr>
					</c:forEach>
										
					<tr class="totals">
						<td>Totals</td>
						<td>${mon:formatPounds(_history.grandTotals.income)}</td>
						<td>${mon:formatPounds(_history.grandTotals.expense)}</td>
						<td></td>
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