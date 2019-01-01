<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
	
<mny:standardLayout>

	<c:set var="_buttonLabel" value="Add account" />
	<c:set var="_pageHeading" value="Add new account" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update account" />
	<c:set var="_pageHeading" value="Update account" />
	</c:if>
	

	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/account/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
			    <tr>
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input type="text" readonly name="identifier" placeholder="Unique id" value="${_account.id}" /></td>
			    </tr>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="name">Name</label></td>
		        <td><input type="text" name="name" placeholder="Enter account name" value="${_account.name}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="type">Type</label></td>
		        <td>
		        	<select name="type">
		        		<c:forTokens items="current,savings,credit,pension" var="_t" delims=",">
		        			<option value="${_t}" <c:if test="${_t eq _account.type}">selected</c:if>>${_t}</option>
		        		</c:forTokens>
		        	</select>
		        </td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="status">Status</label></td>
		        <td>
		        	<select name="status">
		        			<option value="open" <c:if test="${not _account.closed}">selected</c:if>>open</option>
		        			<option value="closed" <c:if test="${_account.closed}">selected</c:if>>closed</option>
		        	</select>
		        </td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="opening">Opening balance</label></td>
		        <td><input type="text" name="opening" placeholder="Enter opening balance in pounds and pence" 
		        	value="${mon:formatPounds(_account.openingBalance)}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="note">Notes</label></td>
		        <td><textarea cols="40" rows="3" name="note" 
		        	placeholder="Enter relevant notes, eg. account number">${_account.note}</textarea></td>
		    </tr>
			</table> 
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete account?" id="delete-button" /> 
	    </c:if>
	    <input type="hidden" name="id" value="${_account.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<script>
$(function() {
	$("#delete-dialog").dialog({
		autoOpen: false, 
		modal: true,
		buttons: [
			{
				text: "Cancel",
				icon: "ui-icon-arrowreturnthick-1-w",
				click: function() {
					$(this).dialog("close");
				}
			},
			{
				text: "Delete",
				icon: "ui-icon-alert",
				click: function() {
					window.location = webContext + "/account/delete/" + ${_account.id} + "?t=" + ${_timestamp};
				}
			}
		]
	});
	
	$("#delete-button").click(function(e){
		$("#delete-dialog").dialog("open");
	});

});
</script>

<div id="delete-dialog" title="Delete account">
	Deleting an account will also delete ALL transactions to/from that account. Are you sure
	you wish to proceed?
</div>
