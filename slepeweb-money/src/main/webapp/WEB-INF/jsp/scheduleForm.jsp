<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
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

	<mny:tsformLabels entityName="schedule" />  <%-- Defines variables _buttonLabel and _pageHeading --%>
	
	<c:if test="${_formMode eq 'update'}">
		<div class="right">
			<a href="${_ctxPath}/schedule/add" title="Define a new scheduled transaction">New schedule</a><br />
		</div>
	</c:if>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form id="schedule-form" method="post" action="${_ctxPath}/schedule/save">	  
	    <table>
	    
		    <mny:tsformIds entity="${_schedule}" />
		    
		    <tr>
		        <td class="heading"><label for="label">Name</label></td>
		        <td>
		        	<input id="label" type="text" name="label" placeholder="Provide an identifier for this schedule" value="${_schedule.label}">
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="period">Interval</label></td>
		        <td>
		        	<input id="period" type="text" name="period" placeholder="Specify interval between scheduled transactions" 
		        		value="${mon:tertiaryOp(_schedule.period, _schedule.period, '1m')}">
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="nextdate">Next date</label></td>
		        <td>
		        	<input id="nextdate" type="text" name="nextdate" class="datepicker" placeholder="Next scheduled date" 
		        		value="${mon:formatTimestamp(_schedule.nextDate)}">
		        </td>
		    </tr>

		    <mny:tsformAccount entity="${_schedule}" />
		    <mny:tsformPaymentType entity="${_schedule}" />
		    <mny:tsformTransfer istransfer="${_schedule.transfer}" mirror="${_schedule.mirror}" />
		    <mny:tsformPayee entity="${_schedule}" />
		    <mny:tsformCategory entity="${_schedule}" />
		    <mny:tsformSubCategory entity="${_schedule}" />
		    <mny:tsformSplits />
		    <mny:tsformNotesAndAmount entity="${_schedule}" />
		    
		    <tr>
		        <td class="heading"><label for="enabled">Enabled</label></td>
		        <td><input id="enabled" type="checkbox" name="enabled"
		        	${mon:tertiaryOp(_formMode eq 'add' or _schedule.enabled, 'checked=checked', '')}  /></td>
		    </tr>
		    
		</table> 
		
		<mny:tsformTail entity="${_schedule}" label="Delete scheduled transaction?" />
	</form>	
	
	<mny:tsformDialogs entity="${_schedule}" />
	
</mny:standardLayout>


