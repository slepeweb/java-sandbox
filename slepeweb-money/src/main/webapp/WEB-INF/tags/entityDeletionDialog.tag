<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="mode" required="true" rtexprvalue="true" %><%@ 
	attribute name="id" required="true" rtexprvalue="true" %><%@ 
	attribute name="level" required="false" rtexprvalue="true" %>

<!-- entityDeletionDialog.tag -->

<%-- Decided to leave this javascript in a jsp, as it uses ${jsp} expressions --%>

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
					window.location = webContext + "/${entity}/delete/" + "${id}";
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

<div id="delete-dialog" title="Delete ${entity}">
	Please NOTE that ALL deletions are FINAL, and CANNOT be undone.<br />
	<c:choose><c:when test="${entity eq 'transaction'}">
		Are you sure you wish to delete this transaction?
	</c:when><c:when test="${entity eq 'chart'}">
		Are you sure you wish to delete this chart?
	</c:when><c:when test="${entity eq 'search'}">
		Are you sure you wish to delete this search?
	</c:when><c:when test="${entity eq 'schedule'}">
		Are you sure you wish to delete this schedule?
	</c:when><c:otherwise>
		Deleting this ${level}${entity} will also delete __N__ corresponding transactions. Are you sure
		you wish to proceed?
	</c:otherwise></c:choose>
</div>
