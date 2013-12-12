<%@ tag language="java" %>
<%@ tag import="com.mediasurface.client.*" %>
<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ attribute name="fieldName" required="true" %>
<ms:defineObjects/>
<% 
	String fieldValue = "";
	try
	{
		fieldValue = ((IItem)request.getAttribute("requestItem")).getFieldValue(fieldName);
	}
	catch (Exception e)
	{
		fieldValue = "The field named [" + fieldName + "] is not available on this item.";
	}
%><%=fieldValue %>
