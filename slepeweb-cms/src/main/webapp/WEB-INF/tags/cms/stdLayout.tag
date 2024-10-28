<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${true}">

		<div id="header-wrapper">
			<cms:navigation-top />
		</div>
		
		<div id="main-wrapper">
			<div class="readonly-layer"></div>
			
			<jsp:doBody />

			<div id="wysiwyg-wrapper">
				<div id="wysiwyg-toolbar"></div>
				<div id="wysiwyg-editor"></div>				
				<div id="wysiwyg-close-icon">
					<i class="fa-regular fa-rectangle-xmark fa-2x"></i>
				</div>
			</div>
			
			<div id="widefield-wrapper">
				<div id="widefield-toolbar"></div>
				<textarea id="widefield-editor"></textarea>				
				<div id="widefield-close-icon">
					<i class="fa-regular fa-rectangle-xmark fa-2x"></i>
				</div>
			</div>
		</div>
	
		<cms:dialogs />
		
</cms:basicLayout>