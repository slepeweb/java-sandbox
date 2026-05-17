<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %>

<!-- listEntryDeletiionDialog.tag -->

<div id="delete-dialog" title="Delete ${entity}">
	Please NOTE that ALL deletions are FINAL, and CANNOT be undone.<br />
	Are you sure you wish to proceed?
</div>