<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/version.tag --></cms:debug>
	
<div>
	<button id="reindex-button" type="button"
		title="Refresh the search index for this item and all its descendants">Re-index</button>
</div>

<div>
	<button id="trash-show-button" type="button"
		title="Reveal the contents of the trash bin">Show bin ...</button>
	<div id="trash-container"></div>
</div>
