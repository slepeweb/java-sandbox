<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/version.tag --></cms:debug>
	
<c:if test="${_showVersionTab}">
	<div id="version-tab">
			<c:choose><c:when test="${editingItem.published}">
				<button id="version-button" type="button"
					title="Click button to create a new version">Version</button>
			</c:when><c:otherwise>
				<button id="version-button-disabled" class="disabled" type="button"
					title="Cannot version an un-published item">Version</button>
			</c:otherwise></c:choose>
			
			<c:if test="${editingItem.version > 1}">
				<button id="revert-button" type="button"
					title="Click button to revert to previous version">Revert</button>
			</c:if>
	</div>
</c:if>
	