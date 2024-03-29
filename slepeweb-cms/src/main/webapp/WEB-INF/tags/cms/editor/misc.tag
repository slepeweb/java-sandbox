<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/misc.tag --></cms:debug>

<p><span class="in-your-face-text">NOTE</span> that the bulk operations offered in this section can NOT be reversed using the undo/redo functionality - that facility,
	only works for changes made to one item at a time.</p>

<div id="misc-accordion">

	<c:if test="${not editingItem.shortcut}">
		<h3>Section operations</h3>
			<div>
				<p>Use with <strong>caution</strong> because these operations will apply 
					to '${editingItem.name}' and <strong>ALL</strong> its descendants 
					(total = <strong>${_numItemsInSection} items</strong>).</p>
				
				<div class="section-ops">
					<div>
						<div class="radio-pair"><input type="radio" name="publish_option" value="publish" /> <span class="radio-label">Publish</span></div>
						<div class="radio-pair"><input type="radio" name="publish_option" value="unpublish" /> <span class="radio-label">Un-publish</span></div>
					</div>
					<div><button id="publish-button" type="button" disabled="disabled">Submit</button></div>
					<div id="publish-progressbar" class="progressbar"></div>
				</div>
				
				<div class="section-ops space-top">
					<div>
						<div class="radio-pair"><input type="radio" name="searchable_option" value="searchable" /> <span class="radio-label">Searchable</span></div>
						<div class="radio-pair"><input type="radio" name="searchable_option" value="not-searchable" /> <span class="radio-label">Un-searchable</span></div>
						<div class="radio-pair"><input type="radio" name="searchable_option" value="re-index" /> <span class="radio-label">Re-index</span></div>
					</div>
					<div><button id="reindex-button" type="button" disabled="disabled">Submit</button></div>
					<div id="reindex-progressbar" class="progressbar"></div>
				</div>
			</div>
	</c:if>
	
	<h3>Flagged Item Operations</h3>
	<div id="flagged-items-section-wrapper">
		<p><strong>There are <span class="num-flags">N</span> flagged items</strong></p>
		
		<edit:flaggedItems />
	</div>
	
	<h3>Trash Bin</h3>
	<div id="trash-container"></div>
</div>
