<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="mode" required="true" rtexprvalue="true" %><%@ 
	attribute name="id" required="true" rtexprvalue="true" %>

<script>
$(function() {
	$("#delete-dialog").dialog({
		autoOpen: false, 
		modal: true,
		buttons: [
			{
				text: "Cancel",
				icon: "ui-icon-arrowreturnthick-1-w",
				click: function() {
					$(this).dialog("close");
				}
			},
			{
				text: "Delete",
				icon: "ui-icon-alert",
				click: function() {
					window.location = webContext + "/${entity}/delete/" + ${id} + "?t=" + ${_timestamp};
				}
			}
		]
	});
	
	$("#delete-button").click(function(e){
		var d = $("#delete-dialog");
		var s = d.html();
		d.html(s.replace("__N__", "${_numDeletableTransactions}"));
		d.dialog("open");
	});

});
</script>

<c:if test="${mode eq 'update' and (_isAdmin or _numDeletableTransactions eq 0)}">
	<div id="delete-dialog" title="Delete ${entity}">
		Deleting this ${entity} will also delete __N__ corresponding transactions. Are you sure
		you wish to proceed?
	</div>
</c:if>
