<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/misc.tag --></cms:debug>

<c:if test="${not editingItem.shortcut}">
	<h2>Section operations</h2>
	<p>Use with a little caution because these operations will apply 
	to '${editingItem.name}' and ALL its descendants (total = ${_numItemsInSection} items).</p>
	
	<div class="section-ops">
		<div>
			<div class="radio-pair"><input type="radio" name="publish_option" value="publish" /> <span class="radio-label">Publish</span></div>
			<div class="radio-pair"><input type="radio" name="publish_option" value="unpublish" /> <span class="radio-label">Un-publish</span></div>
		</div>
		<div><button id="publish-button" type="button" disabled="disabled">Submit</button></div>
		<div id="publish-progressbar" class="progressbar"></div>
	</div>
	
	<div class="section-ops">
		<div>
			<div class="radio-pair"><input type="radio" name="searchable_option" value="searchable" /> <span class="radio-label">Searchable</span></div>
			<div class="radio-pair"><input type="radio" name="searchable_option" value="not-searchable" /> <span class="radio-label">Un-searchable</span></div>
			<div class="radio-pair"><input type="radio" name="searchable_option" value="re-index" /> <span class="radio-label">Re-index</span></div>
		</div>
		<div><button id="reindex-button" type="button" disabled="disabled">Submit</button></div>
		<div id="reindex-progressbar" class="progressbar"></div>
	</div>
	
	<div class="section-ops delete">
		<div>
			<p>Clicking the 'Delete' button will delete <strong>'<span class="current-item-name">${editingItem.name}</span>'</strong> and <strong>ALL</strong> its descendants, 
				making a total of <strong>${_numItemsInSection}</strong> deletions.</p>
		</div>
		<div><button id="trash-button" type="button">Delete</button></div>
		<div></div>
	</div>
	
	<hr />
	<br />
</c:if>

<h2>Miscellaneous operations</h2>

<div>
	<button id="trash-show-button" type="button"
		title="Reveal the contents of the trash bin">Show bin ...</button>
	<div id="trash-container"></div>
</div>
