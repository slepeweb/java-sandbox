<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/media.tag --></cms:debug>
	
<form id="media-form" enctype="multipart/form-data">
	<div class="media-details">
		<div>
			<p><strong>Main media:</strong></p>
			<p><strong>${editingItem.type.mimeType}, ${cmsf:formatBytes(editingItem.media.size)}</strong></p>
			
			<p><u>File locations</u>:</p>
			
			<table>
				<tr>
					<td>Media:</td>
					<td>
						<c:choose><c:when test="${editingItem.mainMediaWithBinaryContent}">
							<strong>${editingItem.media.folder}</strong>/${editingItem.media.repositoryFileName}
						</c:when><c:otherwise>n/a</c:otherwise></c:choose>
					</td>
				</tr>
							
				<tr>
					<td>Thumbnail:</td>
					<td>
						<c:choose><c:when test="${editingItem.thumbnailWithBinaryContent}">
							<strong>${editingItem.media.folder}</strong>/${editingItem.thumbnail.repositoryFileName}
						</c:when><c:otherwise>n/a</c:otherwise></c:choose>
					</td>
				</tr>
			</table>
		</div>

		<div>
			<p><strong>Thumbnail:</strong></p>
			<c:choose><c:when test="${editingItem.thumbnailWithBinaryContent}">
				<%-- 
					Both the editorial and delivery hosts are capable of rendering image data, but
					this image is served from the editorial host, enabling un-published media to be displayed.
				--%>
				<c:set var="_host" value="${editingItem.site.editorialHost}" />
				<c:set var="_timestamp" value="${cmsf:now()}" />
				<img src="${_host.namePortAndProtocol}/cms/stream/itemid/${editingItem.id}/${editingItem.type.shortMimeType}?view=thumbnail&_=${_timestamp}" />
			</c:when><c:otherwise>
				<p>No thumbnail uploaded.</p>
			</c:otherwise></c:choose>
		</div>
	</div>
	
	<div class="ff">&nbsp;</div>
	
	<div class="ff">
		<label>Choose file to upload</label>
		<div class="inputs"><input name="media" type="file" /></div>
	</div>
	
	<c:if test="${editingItem.type.image or editingItem.type.video}">
		<div class="ff">
			<label>Thumbnail?</label>
			<div class="inputs">
				<span>
					None<input name="thumbnail" type="radio" value="none" data-mimetype="${editingItem.type.mimeType}" checked="checked" />
					File IS thumbnail<input name="thumbnail" type="radio" value="onlythumb" data-mimetype="${editingItem.type.mimeType}" />
					
					<c:if test="${editingItem.type.image}">
						Autoscale<input name="thumbnail" type="radio" value="autoscale" data-mimetype="${editingItem.type.mimeType}" />
					</c:if>
				</span>
			</div>
		</div>
		
		<div class="ff hide thumbnail-option">
			<label>Width (px)</label>
			<div class="inputs"><input name="width" type="text" value="200" /></div>
		</div>
	</c:if>
	
	<div class="button-set">
		<button class="action" type="button" disabled="disabled">Upload</button>
		<button class="reset" type="button" disabled="disabled">Reset form</button>
	</div>
	
	<progress class="spacer10" value="0">0%</progress>		
</form>
	