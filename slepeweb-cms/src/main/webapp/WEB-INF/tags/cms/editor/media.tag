<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/media.tag --></cms:debug>
	
<c:if test="${editingItem.type.media}">
	<div id="media-tab">
		<form id="media-form" enctype="multipart/form-data">
			<div class="ff">
				<label for="media">&nbsp;</label>
				<input name="media" type="file" />
			</div>
			<div class="ff">
				<label>&nbsp;</label><button id="media-button" type="button">Update media</button>
			</div>
			<label>&nbsp;</label><progress class="spacer10"></progress>		
		</form>
	</div>
</c:if>
	