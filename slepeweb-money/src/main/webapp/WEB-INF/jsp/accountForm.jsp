<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>

	<c:set var="_flashMessage" value="" />
	<c:set var="_flashType" value="" />
	<c:if test="${not empty param.flash}">
		<c:set var="_flashMessage" value="${fn:substringAfter(param.flash, '|')}" />
		<c:set var="_flashType" value="${fn:substringBefore(param.flash, '|')}" />
	</c:if>

	<h2>Edit account details <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/account/update">	  
	    <table>
		    <tr>
		        <td class="heading"><label for="identifier">Id</label></td>
		        <td><input disabled="disabled" name="identifier" placeholder="Unique id" value="${_account.id}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="name">Name</label></td>
		        <td><input name="name" placeholder="Account name" value="${_account.name}" /></td>
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
		        <td><input name="opening" placeholder="Enter opening balance in pounds and pence" 
		        	value="${mon:formatPounds(_account.openingBalance)}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="note">Notes</label></td>
		        <td><textarea cols="40" rows="3" name="note" 
		        	placeholder="Enter relevant notes, eg. account number">${_account.note}</textarea></td>
		    </tr>
			</table> 
			
			<br />
	    <input type="submit" value="Update" /> 
	    <input type="hidden" name="id" value="${_account.id}" />   
	</form>		  	
		
</mny:standardLayout>
