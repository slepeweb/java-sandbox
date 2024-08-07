<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/flaggedItems.tag --></cms:debug>

<div id="flagged-items-section">
	<div id="move-flagged-section" class="section-ops">
		<div><strong>Move ALL</strong> currently flagged items
		
			<span class="gap-1em"><select name="position">
				<option value="none">Choose ...</option>
				<option value="before">Before</option>
				<option value="over">Below</option>
				<option value="after">After</option>
			</select></span>
			
			<div>
				<i class="fa-solid fa-sitemap itempicker gap-1em" title="Pick a target from the content structure"></i>
				<span id="move-target-identifier2">(your chosen target)</span>
			</div>
		</div>
		
		<div><button id="move-flagged-button" disabled="disabled" type="button">Move items</button></div>
	</div>

	<div class="section-ops">
		<p><strong>Trash ALL</strong> currently flagged items:</p>
		<div><button id="trash-button" type="button">Trash ALL</button></div>
	</div>

	<div class="section-ops">
		<p><strong>Copy data</strong> to <strong>ALL</strong> currently flagged items?</p>
		<p id="copy-data-downarrow"><i class="fa-solid fa-angle-down fa-2x"></i></p>
	</div>

	<div id="copy-data-section" class="hide">
		<p>The following data is available for copying to flagged items. 
			Check the boxes corresponding to the data you want to copy, then click the 'Copy ALL' button.
			If you have just updated the core/field data for the current item, you may need to
			click this refresh icon 
			<i class="fa-solid fa-rotate-right refresher" title="Update form if needs be"></i>
			to reflect those changes in this form.
		</p>
		
		<div id="copy-flagged-data-form">
			<edit:copyFlaggedForm />
		</div>
	</div>
			
</div>
