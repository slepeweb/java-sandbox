<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/misc.tag --></cms:debug>

<h2>Section operations</h2>
<p>Use with a little caution because these operations will apply 
to '${editingItem.name}' and ALL its descendants (total = ${_numItemsInSection} items).</p>

<div class="section-ops">
	<div>
		<div class="radio-pair"><input type="radio" name="publish_option" value="publish" /> <span>Publish</span></div>
		<div class="radio-pair"><input type="radio" name="publish_option" value="unpublish" /> <span>Un-publish</span></div>
	</div>
	<div><button id="publish-button" type="button" disabled="disabled">Submit</button></div>
	<div id="publish-progressbar" class="progressbar"></div>
</div>

<div class="section-ops">
	<div>
		<div class="radio-pair"><input type="radio" name="searchable_option" value="searchable" /> <span>Searchable</span></div>
		<div class="radio-pair"><input type="radio" name="searchable_option" value="not-searchable" /> <span>Un-searchable</span></div>
		<div class="radio-pair"><input type="radio" name="searchable_option" value="re-index" /> <span>Re-index</span></div>
	</div>
	<div><button id="reindex-button" type="button" disabled="disabled">Submit</button></div>
	<div id="reindex-progressbar" class="progressbar"></div>
</div>

<div class="section-ops delete">
	<div>
		Clicking the 'Delete' button will delete a total of ${_numItemsInSection} items, PLUS any older versions of those items.
	</div>
	<div><button id="trash-button" type="button">Delete</button></div>
	<div></div>
</div>

<hr />
<br />
<h2>Miscellaneous operations</h2>

<div>
	<button id="trash-show-button" type="button"
		title="Reveal the contents of the trash bin">Show bin ...</button>
	<div id="trash-container"></div>
</div>
