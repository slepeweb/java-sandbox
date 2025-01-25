<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="memo" required="true" rtexprvalue="true" %>

<tr>
    <td class="heading"><label for="memo">Notes</label></td>
    <td><input id="memo" type="text" name="memo" placeholder="Enter any relevant notes" value="${memo}" /></td>
</tr>
