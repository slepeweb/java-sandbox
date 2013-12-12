<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<!--
 
This is the callback function that is invoked when
the Component is introduced to the page from either
the PageStudio Toolbar or after configuration of the
Component if it is a ConfigurableComponent.

Best practice would be to abstract any page load
scripting into a function that is called from within
the componentLoadHandler and as well as being set
against the on page load event, thus ensuring that
the same initialization code is called in both cases.

-->
<script language="javascript" type="text/javascript">
componentLoadHandler = function (){
	<!-- place onRender component code here --> 
};

componentLoadHandler();
</script>
<!--
*******
	Component Body
*******
-->
</ps:component>