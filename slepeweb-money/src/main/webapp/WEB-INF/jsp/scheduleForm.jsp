<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- scheduleForm.jsp -->

<c:set var="_extraInPageJs" scope="request">
	_money.context = 'schedule';
</c:set>

<c:set var="_extraJs" scope="request" value="schedule.js,transandsched.js,minorcats.js,datepicker.js" />

<c:set var="_extraInPageCss" scope="request">
	.ui-autocomplete-loading {
		background: white url("${_ctxPath}/resources/images/progress-indicator.gif") right center no-repeat;
	}
</c:set>

<mny:flash />
	
<mny:standardLayout>

	<tsf:labels entityName="schedule" />  <%-- Defines variables _buttonLabel and _pageHeading --%>
	
	<c:if test="${_formMode eq 'update'}">
		<div class="right">
			<a href="${_ctxPath}/schedule/add" title="Define a new scheduled transaction">New schedule</a><br />
		</div>
	</c:if>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form id="schedule-form" method="post" action="${_ctxPath}/schedule/save">	  
	    <table>
	    
		    <tsf:ids entity="${_schedule}" />
		    
	    	<mny:tableRow heading="Name">
		    	<input id="label" type="text" name="label" placeholder="Provide an identifier for this schedule" value="${_schedule.label}">
				</mny:tableRow>

	    	<mny:tableRow heading="Interval">
		    	<input id="period" type="text" name="period" placeholder="Specify interval between scheduled transactions" 
		        		value="${mon:tertiaryOp(_schedule.period, _schedule.period, '1m')}">
				</mny:tableRow>

	    	<mny:tableRow heading="Next date">
		    	<input id="nextdate" type="text" name="nextdate" class="datepicker" placeholder="Next scheduled date" 
		        		value="${mon:formatTimestamp(_schedule.nextDate)}">
		    </mny:tableRow>

		    <tsf:account accountId="${_schedule.account.id}" />
		    <tsf:paymentType entity="${_schedule}" />
		    <tsf:transfer istransfer="${_schedule.transfer}" mirror="${_schedule.mirror}" />
		    <tsf:payee payeeName="${_schedule.payee.name}" />
		    <tsf:category entity="${_schedule}" />
		    <tsf:subCategory entity="${_schedule}" />
				<mny:categoryList heading="Splits" categories="${_transactionSplits}" />
		    <tsf:notes memo="${_schedule.memo}" />
		    <tsf:amount value="${_schedule.amountValue}" isdebit="${_schedule.debit}" />
		    
	    	<mny:tableRow heading="Enabled?">
		    	<input id="enabled" type="checkbox" name="enabled"
		        	${mon:tertiaryOp(_formMode eq 'add' or _schedule.enabled, 'checked=checked', '')}  />
		    </mny:tableRow>
		    
		</table> 
		
		<tsf:tail entity="${_schedule}" label="Delete scheduled transaction?" />
	</form>	
	
	<tsf:dialogs entity="${_schedule}" />
	
</mny:standardLayout>


