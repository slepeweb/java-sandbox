<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<c:if test="${_formMode eq 'update'}">
	<tr class="opaque50">
	    <td class="heading"><label for="identifier">Id</label></td>
	    <td><input id="identifier" type="text" readonly name="identifier" value="${entity.id}" /></td>
	</tr>
	
	<c:if test="${entity.origId gt 0}">
		<tr class="opaque50">
		    <td class="heading"><label for="origid">Original id</label></td>
		    <td><input id="origid" type="text" readonly name="origid" value="${entity.origId}" /></td>
		</tr>
	</c:if>
</c:if>
