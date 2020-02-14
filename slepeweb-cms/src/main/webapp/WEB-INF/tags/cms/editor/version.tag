<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/version.tag --></cms:debug>
	
<c:if test="${_showVersionTab}">
	<div id="version-tab">
		<form>
			<table width="100%">
				<tr>
					<c:choose><c:when test="${editingItem.published}">
						<td><label>Click button to create a new version: </label></td>
						<td><button id="version-button" type="button">Version</button></td>
					</c:when><c:otherwise>
						<td><label>Cannot version an un-published item: </label></td>
						<td><button id="version-button-disabled" class="disabled" type="button">Version</button></td>
					</c:otherwise></c:choose>
				</tr>
				
				<c:if test="${editingItem.version > 1}">
					<tr>
							<td><label>Click button to revert to previous version: </label></td>
							<td><button id="revert-button" type="button">Revert</button></td>
					</tr>
				</c:if>
			</table>
		</form>
	</div>
</c:if>
	