<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/flaggedItems.tag --></cms:debug>

<div>
	<div id="flagged-items-message" class="hide">${_flaggedItemsMessage}</div>
	<h3>You have flagged ${fn:length(_flaggedItems)} item(s):</h3>

	<c:if test="${fn:length(_flaggedItems) > 0}">
		<ul>
			<c:forEach items="${_flaggedItems}" var="_gist">
				<li><a href="#" class="link-to-item" data-id="${_gist.itemId}">${_gist.name}</a> (${_gist.path})</li>
			</c:forEach>
		</ul>
	</c:if>
		
	<div class="section-ops">
		<p><strong>Flag ALL</strong> sibling items:</p>
		<div><button id="flag-siblings-button" type="button">Flag Siblings</button></div>
	</div>
		
	<c:if test="${fn:length(_flaggedItems) > 0}">
	
		<div class="section-ops">
			<p><strong>Unflag ALL</strong> currently flagged items:</p>
			<div><button id="unflag-button" type="button">Unflag ALL</button></div>
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
				Check the boxes corresponding to the data you want to copy, then click the 'Copy' button.</p>
			
			<div class="ff">
				<input type="checkbox" class="copy-core-data" data-name="tags" />
				<label>Tags: </label><input type="text" name="copy-tags" value="${editingItem.tagsAsString}" />
			</div>
							
			<div class="ff">
				<input type="checkbox" class="copy-core-data" data-name="published" />
				<label>Published?: </label><input type="checkbox" name="copy-published" 
					<c:if test="${editingItem.published}">checked="checked"</c:if> />
			</div>
							
			<div class="ff">
				<input type="checkbox" class="copy-core-data" data-name="searchable" />
				<label>Searchable?: </label><input type="checkbox" name="copy-published" 
					<c:if test="${editingItem.searchable}">checked="checked"</c:if> />
			</div>
							
			<c:forEach items="${_fieldSupport[editingItem.language]}" var="fes">
				<div class="ff">
					<input type="checkbox" class="copy-fieldvalue" data-name="${fes.field.variable}" />
					<label>${fes.label} : </label> 
					${fes.inputTag}
				</div>					
			</c:forEach>

			<div class="section-ops">
				<p>Copy selected data to <strong>ALL</strong> currently flagged items:</p>
				<div><button id="copy-data-button" type="button">Copy ALL</button></div>
			</div>
		
		</div>
		
	</c:if>
	
</div>
