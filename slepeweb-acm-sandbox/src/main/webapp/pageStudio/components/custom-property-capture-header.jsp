<%
	String instId = (String) request.getAttribute("instId");
	String compHandlerId = (String) request.getAttribute("compHandlerId");
	String URL_PARAM_COMPONENT_CONFIGURATION = "configuration";
	String URL_PARAM_STORE_PROPERTIES = "storeProperties";
%>
<div class="componentConfiguration">
	<form method="post" id="form_upd_<%= instId %>" action="" >
		<input type="hidden" id=<%= compHandlerId %> name="componentId" value=<%= compHandlerId %> />
		<input type="hidden" name="instanceId" value=<%= instId %> />
		<script language="JavaScript" type="text/javascript">
			submit_component_config = function () {
				
				if (pageStudioTool.selectedOverlay != null) {
					var cid = pageStudioTool.selectedField._cid;
					var cInstId =  "<%= instId %>";
					var formContents = Form.serialize("form_upd_<%= instId %>");
					new Ajax.Updater(pageStudioTool.selectedField, '/ps-component-factory', {
						method: "get",
						parameters: formContents + "&action=getComponent&<%= URL_PARAM_COMPONENT_CONFIGURATION %>=<%= URL_PARAM_STORE_PROPERTIES %>&cid="+encodeURI(cid) + "&cInstId="+encodeURI(cInstId) + "&" + pageStudioTool.addRequestedItemParams(), 
						evalScripts: true,
						onComplete: pageStudioTool.attachFieldEvents.bind(pageStudioTool, pageStudioTool.selectedField)
					});
					
					return false;
				}
				return false;
			}
			</script>