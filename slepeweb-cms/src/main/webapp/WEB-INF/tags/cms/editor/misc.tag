<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/misc.tag --></cms:debug>

<p class="in-your-face-text">NOTE that the bulk operations in this section can NOT be reversed using the undo/redo functionality,
	which only works for one item at a time.</p>

<div id="misc-accordion">
	<h3>Trash current item</h3>
	<div class="section-ops delete">
		<p>Clicking the 'Trash' button will move <strong>'<span class="current-item-name">${editingItem.name}</span>'</strong> 
			and <strong>ALL</strong> its descendants into the trash bin, moving a total of 
			<strong>${_numItemsInSection}</strong> items.</p>
			
		<div><button id="trash-button" type="button">Trash</button></div>
<!-- 		<div></div> -->
	</div>

	<c:if test="${not editingItem.shortcut}">
		<h3>Section operations</h3>
			<div>
				<p>Use with <strong>caution</strong> because these operations will apply 
					to '${editingItem.name}' and <strong>ALL</strong> its descendants (total = ${_numItemsInSection} items).</p>
				
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
	<div id="flagged-items-section">
		<edit:flaggedItems />
	</div>
	
	<h3>Trash Bin</h3>
	<div>
		<button id="trash-show-button" type="button"
			title="Reveal the contents of the trash bin">Show trash bin ...</button>
		<div id="trash-container"></div>
	</div>
</div>
