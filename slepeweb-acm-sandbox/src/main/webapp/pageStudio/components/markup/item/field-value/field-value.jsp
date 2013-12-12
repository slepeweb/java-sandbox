<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ms:defineObjects />
<ps:component>
<%
	String name = propertyPublisher.getProperty("name");
	if(name != null && name.length() > 0) {
	  
    String length = propertyPublisher.getProperty("maxlength");
    int maxLength = length != null && length.length() > 0 ? Integer.parseInt(length) : 0;
	  
		String value;
		try {
		  value = requestItem.getFieldValue(name);
		}
		catch(Exception re) {
		  value = "The request item does not contain a field value with the name '" + name + "'";
		  maxLength = 0;
		}
		
		if(value == null) value = "";
		
		if(maxLength > 0 && value.length() > maxLength) {
			if(maxLength > 4) {
					maxLength = maxLength - 3;
			}
			value = value.substring(0, maxLength) + "...";
		}
		String url = propertyPublisher.getProperty("url");
		if(url != null && url.length() > 0) {
			value = String.format("<a href='%1$s'>%2$s</a>", new Object[] { url, value });
		}
%>
	<%= value %>
<%
	}
%>

</ps:component>