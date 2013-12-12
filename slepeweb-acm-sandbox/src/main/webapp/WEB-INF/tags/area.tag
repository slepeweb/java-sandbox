<%@ tag language="java" %>
<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="original" required="false" rtexprvalue="true"%>

<ms:defineObjects/>
<ps:init/>
<%if(id == null) { 
	id = "ps_no_id";
	original = "true";
}%>
<ps:area id="<%=id%>" original="<%=original%>"/>
<%	
	// This flag is used to enable the PageStudio Toolbar only in contributor
	String isContrib = request.getParameter("ms-in-situ-editing");
	if ((isContrib != null && isContrib.equals("true")))
	{
%>
		<pagestudio:tool id="<%=id%>" />
<%
	}
%>
