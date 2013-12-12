<%@ page import="com.mediasurface.datatypes.ItemKey" %>
<%@ page import="com.mediasurface.client.IItem" %>
<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ms:defineObjects/>
<ps:component>
	<%
		// Retrieve the configured 'item' property for this component instance. 
		ItemKey itemKey = propertyPublisher.getPropertyItemKey("item");
		
		if(itemKey != null)
		{
			String src = null;
			try {
				IItem item = ms.getFromOriginal(ctx, itemKey);
				src = item.getUrl();
			}
			catch(Exception e)
			{}
			if(src != null)
			{
	%>
				<img src="<%= src %>" style="float:left; max-width: 100px;" />
	<%
			}
		}
	%>
</ps:component>