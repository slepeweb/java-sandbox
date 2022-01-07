<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/core.tag --></cms:debug>
	
<form id="item-core-editor">
	<%-- Javascript looks for this input element --%>
	<input id="itemIsProductFlag" type="hidden" value="${editingItem.product}" />
	
	<c:set var="_idStr" value="${editingItem.id}" />
	<c:if test="${editingItem.version > 1}"><c:set var="_idStr" value="${editingItem.id} (Orig. ${editingItem.origId})" /> </c:if>
	
	<div class="ff">
		<label for="id">Id: </label><input disabled="disabled" value="${_idStr}" />
	</div>
	
	<div class="ff">
		<label for="path">Path: </label><input disabled="disabled" value="${editingItem.path}" />
		
		<c:if test="${not empty _host}">
			<c:set var="url" value="${editingItem.path}" />
			<c:if test="${editingItem.site.multilingual}"><c:set var="url" value="/${editingItem.site.language}${url}" /></c:if>
			<a href="${_host.namePortAndProtocol}${url}" target="_blank">View</a>
		</c:if>		
	</div>
	
	<div class="ff">
		<label for="type">Type: </label><input disabled="disabled" value="${editingItem.type.name}" />
	</div>
	<div class="ff">
		<label for="dateupdated">Date last updated: </label><input disabled="disabled" value="${editingItem.dateUpdated}" />
	</div>
	<div class="ff">
		<label for="version">Version: </label><input disabled="disabled" value="${editingItem.version}" />
	</div>
	<div class="ff">
		<label for="type">Template: </label>
		<select name="template" <c:if test="${not editingItem.page}">disabled</c:if>>
			<option value="0">Choose ...</option>
			<c:forEach items="${availableTemplatesForType}" var="template">
				<option value="${template.id}"<c:if 
					test="${not empty editingItem.template and 
						template.id eq editingItem.template.id}"> selected</c:if>>${template.name}</option>
			</c:forEach>
		</select>
	</div>
	<div class="ff">
		<label for="name">Name: </label><input type="text" name="name" value="${editingItem.name}" />
	</div>
	<div class="ff">
		<label for="simplename">URL slug: </label><input type="text" name="simplename" value="${editingItem.simpleName}" <c:if 
			test="${editingItem.root}">disabled="disabled"</c:if> />
	</div>
	
	<%-- This div will only be visible if the selected item type is Product --%>
	<c:if test="${editingItem.product}">
		<div class="ff">
			<label for="partNum">Part number: </label><input type="text" name="partNum" value="${editingItem.partNum}" />
		</div>
		<div class="ff">
			<label for="price">Price: </label><input type="text" name="price" value="${editingItem.priceInPoundsAsString}" />
		</div>
		<div class="ff">
			<label for="stock">Stock: </label><input type="text" name="stock" value="${editingItem.stock}" />
		</div>
		
		<c:choose><c:when test="${not editingItem.hasVariants}">
			<div class="ff">
				<label for="alphaaxis">Axis A: </label>
				<select name="alphaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
			<div class="ff">
				<label for="betaaxis">Axis B: </label>
				<select name="betaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.betaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
		</c:when><c:otherwise>
			<div class="ff">
				<label>Axis A: </label>
				<input disabled="disabled" value="${editingItem.alphaAxis.shortname}" />
			</div>
			<div class="ff">
				<label>Axis B: </label>
				<input disabled="disabled" value="${editingItem.betaAxis.shortname}" />
			</div>
		</c:otherwise></c:choose>
	</c:if>
	
	<div class="ff">
		<label for="tags">Tags: </label><input type="text" name="tags" value="${editingItem.tagsAsString}" />
		<span><i id="tags-menu-icon" class="fas fa-bars"></i></span>
		<div id="tag-options" class="hide">
			<div>
				<h3>Recent</h3>
				<ul>
					<c:forEach items="${_tis.recent}" var="_tagval">
						<li>${_tagval}</li>
					</c:forEach>
				</ul>
			</div>
			
			<div>
				<h3>All</h3>
				<ul>
					<c:forEach items="${_tis.all}" var="_tagval">
						<li>${_tagval}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	
	<c:set var="_disabled"></c:set>
	<c:if test="${editingItem.shortcut}"><c:set var="_disabled">disabled="disabled"</c:set></c:if>
	<div class="ff">
		<label for="searchable">Searchable? </label><input ${_disabled} type="checkbox" name="searchable" <c:if test="${editingItem.searchable}">checked="checked"</c:if> />
	</div>
	
	<div class="ff">
		<label for="published">Published? </label><input ${_disabled} type="checkbox" name="published" <c:if test="${editingItem.published}">checked="checked"</c:if> />
	</div>
		
	<div class="button-set">
		<button class="action" type="button" disabled="disabled" title="Update changes to core data">Update</button>
		<button class="reset" type="button" disabled="disabled">Reset form</button>
	</div>
</form>
	