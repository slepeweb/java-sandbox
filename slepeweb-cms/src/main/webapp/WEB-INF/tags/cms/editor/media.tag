<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/media.tag --></cms:debug>
	
<form id="media-form" enctype="multipart/form-data">
	<div class="ff">
		<label>Current media</label>
		<c:choose><c:when test="${editingItem.mediaLoaded}">
			<c:choose><c:when test="${editingItem.type.image}">
				<%-- TODO: Store port info on db? What about server type, ie staging vs. live ? --%>
				<img src="http://${host.name}:8080${editingItem.url}" width="200" />
			</c:when><c:otherwise>
				Loaded (${editingItem.type.mimeType})
			</c:otherwise></c:choose>
		</c:when><c:otherwise>
			None
		</c:otherwise></c:choose>
	</div>
	
	<div class="ff">
		<label>Choose file to upload</label>
		<input name="media" type="file" />
	</div>
	
	<c:if test="${editingItem.type.image}">
		<div class="ff">
			<label>Thumbnail required?</label>
			<input name="thumbnail" type="checkbox" />
		</div>
		
		<div class="ff hide thumbnail-option">
			<label>Width (px)</label>
			<input name="width" type="text" value="200" />
		</div>
	</c:if>
	
	<div class="ff">
		<button id="media-button" type="button" disabled="disabled">Upload</button>
	</div>
	
	<progress class="spacer10" value="0">0%</progress>		
</form>
	