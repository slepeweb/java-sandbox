<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="trclass" required="false" rtexprvalue="true" %><%@ 
	attribute name="tdclass" required="false" rtexprvalue="true" %>

<c:set var="trclass_">${not empty trclass ? trclass : ''}</c:set>
<c:set var="tdclass_">${not empty tdclass ? tdclass : ''}</c:set>

<tr class="${trclass_}">
    <td class="heading ${tdclass_}"><label>${heading}</label></td>
    <td><jsp:doBody /></td>
</tr>
	