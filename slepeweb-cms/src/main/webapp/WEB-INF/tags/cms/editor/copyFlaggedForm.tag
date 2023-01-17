<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/copyFlaggedForm.tag --></cms:debug>

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
				
<c:forEach items="${_fieldSupport[editingItem.site.language]}" var="fes">
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
