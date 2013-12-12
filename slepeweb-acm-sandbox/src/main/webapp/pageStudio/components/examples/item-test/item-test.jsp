<%@ page import="com.mediasurface.datatypes.ItemKey" %>
<%@ page import="com.mediasurface.client.IItem" %>
<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ms:defineObjects/>
<ps:component>
	<%
		ItemKey[] keys = propertyPublisher.getPropertyItemKeyList("item");
		if(keys != null)
		{
			for(int i = 0; i < keys.length; i++)
			{
				String src = null;
				try {
					IItem item = ms.getFromOriginal(ctx, keys[i]);
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
		}
	%>
</ps:component>