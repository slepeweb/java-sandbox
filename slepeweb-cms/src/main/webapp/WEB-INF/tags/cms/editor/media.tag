<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/media.tag --></cms:debug>
	
<form id="media-form" enctype="multipart/form-data">
	<div class="ff">
		<label for="media">&nbsp;</label>
		<input name="media" type="file" />
	</div>
	<div class="ff">
		<label>&nbsp;</label><button id="media-button" type="button">Update media</button>
	</div>
	<label>&nbsp;</label><progress class="spacer10" value="0">0%</progress>		
</form>
	