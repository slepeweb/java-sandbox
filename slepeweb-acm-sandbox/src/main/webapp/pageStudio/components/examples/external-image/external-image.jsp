<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<script language="javascript" type="text/javascript">
componentLoadHandler = function (){
};

componentLoadHandler();
</script>
<div>
	<div style="align:left;">
	    <span>The external image component<br/>Shows an image below</span>
	</div>
    <img src="<%= propertyPublisher.getProperty("selection") %>" alt="<%= propertyPublisher.getProperty("alt") %>" />
</div>
</ps:component>
