<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/media.tag --></cms:debug>
	
<form id="media-form" enctype="multipart/form-data">
	<div class="ff">
		<c:choose><c:when test="${editingItem.mediaLoaded}">
			<label>Current media (${editingItem.type.mimeType}, ${cmsf:formatBytes(editingItem.media.size)})
			<c:if test="${editingItem.media.fileStored}">
				<c:set var="_filePath" value="${editingItem.media.folder}/${editingItem.media.repositoryFileName}" />
				<br /><br />File stored @ ${_filePath}
			</c:if>
			</label>

			<c:choose><c:when test="${editingItem.type.image or editingItem.type.video}">
				<c:set var="_host" value="${editingItem.site.stagingHost}" />
				<c:choose><c:when test="${editingItem.thumbnailLoaded}">
					<img src="${_host.protocol}://${_host.name}:${_host.port}/cms/media${editingItem.path}?view=thumbnail" />
				</c:when><c:otherwise>
					<c:if test="${editingItem.type.image}">
						<img src="${_host.protocol}://${_host.name}:${_host.port}/cms/media${editingItem.path}" width="200" />
					</c:if>
				</c:otherwise></c:choose>
			</c:when><c:otherwise>
				Loaded (${editingItem.type.mimeType})
			</c:otherwise></c:choose>
		</c:when><c:otherwise>
			<label>No media loaded</label>
		</c:otherwise></c:choose>
	</div>
	
	<div class="ff">
		<label>Choose file to upload</label>
		<input name="media" type="file" />
	</div>
	
	<c:if test="${editingItem.type.image or editingItem.type.video}">
		<div class="ff">
			<label>Thumbnail?</label>
			<span>
				None<input name="thumbnail" type="radio" value="none" data-mimetype="${editingItem.type.mimeType}" checked="checked" />
				File IS thumbnail<input name="thumbnail" type="radio" value="onlythumb" data-mimetype="${editingItem.type.mimeType}" />
				
				<c:if test="${editingItem.type.image}">
					Autoscale<input name="thumbnail" type="radio" value="autoscale" data-mimetype="${editingItem.type.mimeType}" />
				</c:if>
			</span>
		</div>
		
		<div class="ff hide thumbnail-option">
			<label>Width (px)</label>
			<input name="width" type="text" value="200" />
		</div>
	</c:if>
	
	<div class="button-set ff">
		<button class="action" type="button" disabled="disabled">Upload</button>
		<button class="reset" type="button" disabled="disabled">Reset form</button>
	</div>
	
	<progress class="spacer10" value="0">0%</progress>		
</form>
	