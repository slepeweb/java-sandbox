<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ms" uri="mediasurfaceTags" %>
<%@page import="com.mediasurface.datatypes.ItemKey" %>
<%@page import="com.mediasurface.client.IItem" %>
<%@page import="com.mediasurface.client.taglib.MaeUtils" %>
<ms:defineObjects />
<ps:component>
	<style type="text/css">
		/* The button created by the SelectItemTag will have a class (.ps_select_item_button)
		and an ID based on the name of the property name you have passed it (#ps_select_item_button_<the name>).*/
		#ps_select_item_button_item { float: left; }
	</style>
	<script type="text/javascript">
		//It is important to define the callback function in a way that will be visible outside of the global scope as when the component is
		//loaded, 
		window.onItemSelected = function(result) {
			if(result && result.length > 0) {
				/*
				* The callback is passed an array of JSON objects containing the key,
				* original key and name of the selected item.
				* In this example, we are only interested in the first selection;
				* If we were using itemKeyList instead of itemKey, we could handle the whole array.
				* The original key is used to ensure that the component can render new versions of the
				* selected item.
				*/
				var selection = result[0];
				document.getElementById("item_name").value = unescape(selection.name);
				document.getElementById("item_key").value = selection.originalkey;
			}
		}
	</script>
	<div class="ps_form">
		<h3>Please configure the components details</h3>
		<div style="clear: both; padding-top: 10px;">
			<label for="item_name">Item:</label>
			<input id="item_name" readonly type="text" size="25" style="width: 160px; float: left; clear: left;"/>
			<%
				String key = propertyPublisher.getProperty("item");
			%>
			<ps:selectItem cid="<%= propertyPublisher.getComponentHandler().getId() %>" name="item" label="Browse..." callback="window.onItemSelected" />
			<input id='item_key' type='hidden' name='item' value="<%=key%>"/>
		</div>
	</div>
	
	<script type="text/javascript">
		
	<%  //If we have a key value from a previous configuration, we can set the item name.
		if(!key.equals("")) {
		ItemKey itemKey = new ItemKey(Integer.parseInt(key));
		IItem item = MaeUtils.getMediasurface(pageContext).getFromOriginal(MaeUtils.getSecurityContextHandle(pageContext), itemKey);
	%>
		document.getElementById("item_name").value = "<%= item.getFullName() %>";
	<%}%>
	</script>
</ps:component>