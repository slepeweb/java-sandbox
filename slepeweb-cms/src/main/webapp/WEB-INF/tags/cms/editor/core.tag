<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/core.tag --></cms:debug>
	
<form id="item-core-editor">
	<%-- Javascript looks for this input element --%>
	<input id="itemIsProductFlag" type="hidden" value="${editingItem.product}" />
	
	<c:set var="_idStr" value="${editingItem.id}" />
	<c:if test="${editingItem.version > 1}"><c:set var="_idStr" value="${editingItem.id} (Orig. ${editingItem.origId})" /> </c:if>
	
	<div class="ff">
		<label for="id">Id: </label><div class="inputs"><input disabled="disabled" value="${_idStr}" /></div>
	</div>
	
	<div class="ff">
		<label for="path">Path: </label><div class="inputs"><input disabled="disabled" value="${editingItem.path}" /></div>
		
		<c:if test="${editingItem.published}">		
			<%--
				The page for this item can only be served by the delivery host. 
				The editorial host has no such capability.
				NOTE that the delivery host can only render published items, hence the surrounding <c:if> block.
			--%>
			<c:set var="_host" value="${editingItem.site.deliveryHost}" />
			
			<c:set var="url" value="${editingItem.path}" />
			<c:if test="${editingItem.site.multilingual}"><c:set var="url" value="/${editingItem.site.language}${url}" /></c:if>
			<div class="extras"><a href="${_host.namePortAndProtocol}${url}" target="_blank">View published page</a></div>
		</c:if>
	</div>
	
	<div class="ff">
		<label for="type">Type: </label><div class="inputs"><input disabled="disabled" value="${editingItem.type.name}" /></div>
	</div>
	
	<div class="ff">
		<label for="dateupdated">Date last updated: </label><div class="inputs"><input disabled="disabled" value="${editingItem.dateUpdated}" /></div>
	</div>
	
	<div class="ff">
		<label for="version">Version: </label><div class="inputs"><input disabled="disabled" value="${editingItem.version}" /></div>
	</div>
	
	<div class="ff">
		<label for="type">Template: </label>
		<div class="inputs">
			<select name="template" <c:if test="${not editingItem.page}">disabled</c:if>>
				<option value="0">Choose ...</option>
				<c:forEach items="${availableTemplatesForType}" var="template">
					<option value="${template.id}"<c:if 
						test="${not empty editingItem.template and 
							template.id eq editingItem.template.id}"> selected</c:if>>${template.name}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	
	<div class="ff">
		<label>Owner: </label>
		
		<div class="inputs">
			<c:choose><c:when test="${not _ownership.updateable}">
				<input disabled="disabled" value="${_ownership.owner.fullName}" />
			</c:when><c:otherwise>
				<select name="owner">
					<c:forEach items="${_ownership.siteContributors}" var="_siteContributor">
						<option value="${_siteContributor.id}"
							<c:if test="${_siteContributor.id == _ownership.owner.id}">selected="selected"</c:if>>
								${_siteContributor.lastName}, ${_siteContributor.firstName}</option>
					</c:forEach>
				</select>
			</c:otherwise></c:choose>
		</div>
	</div>
	
	<div class="ff">
		<label for="name">Name: </label><div class="inputs"><input type="text" name="name" value="${editingItem.name}" /></div>
	</div>
	
	<div class="ff">
		<label for="simplename">URL slug: </label>
		<div class="inputs"><input type="text" name="simplename" value="${editingItem.simpleName}" <c:if 
			test="${editingItem.root}">disabled="disabled"</c:if> />
		</div>
	</div>
	
	<%-- This div will only be visible if the selected item type is Product --%>
	<c:if test="${editingItem.product}">
		<div class="ff">
			<label for="partNum">Part number: </label><div class="inputs"><input type="text" name="partNum" value="${editingItem.partNum}" /></div>
		</div>
		
		<div class="ff">
			<label for="price">Price: </label><div class="inputs"><input type="text" name="price" value="${editingItem.priceInPoundsAsString}" /></div>
		</div>
		
		<div class="ff">
			<label for="stock">Stock: </label><div class="inputs"><input type="text" name="stock" value="${editingItem.stock}" /></div>
		</div>
		
		<c:choose><c:when test="${not editingItem.hasVariants}">
			<div class="ff">
				<label for="alphaaxis">Axis A: </label>
				<div class="inputs">
					<select name="alphaaxis">
						<option value="-1">Choose ...</option>
						<c:forEach items="${availableAxes}" var="axis">
							<option value="${axis.id}"<c:if 
								test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<div class="ff">
				<label for="betaaxis">Axis B: </label>
				<div class="inputs">
					<select name="betaaxis">
						<option value="-1">Choose ...</option>
						<c:forEach items="${availableAxes}" var="axis">
							<option value="${axis.id}"<c:if 
								test="${axis.id eq editingItem.betaAxisId}"> selected</c:if>>${axis.shortname}</option>
						</c:forEach>
					</select>
				</div>	
			</div>
		</c:when><c:otherwise>
			<div class="ff">
				<label>Axis A: </label>
				<div class="inputs"><input disabled="disabled" value="${editingItem.alphaAxis.shortname}" /></div>
			</div>
			
			<div class="ff">
				<label>Axis B: </label>
				<div class="inputs"><input disabled="disabled" value="${editingItem.betaAxis.shortname}" /></div>
			</div>
		</c:otherwise></c:choose>
	</c:if>
	
	<div class="ff">
		<label for="tags">Tags: </label>
		<div class="inputs"><input type="text" name="tags" value="${editingItem.tagsAsString}" /></div>
		<div class="extras">
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
	</div>
	
	<c:set var="_disabled"></c:set>
	<c:if test="${editingItem.shortcut}"><c:set var="_disabled">disabled="disabled"</c:set></c:if>
	
	<div class="ff">
		<label for="searchable">Searchable? </label>
		<div class="inputs"><input ${_disabled} type="checkbox" name="searchable" <c:if test="${editingItem.searchable}">checked="checked"</c:if> /></div>
	</div>
	
	<div class="ff">
		<label for="published">Published? </label><div class="inputs"><input ${_disabled} type="checkbox" name="published" <c:if test="${editingItem.published}">checked="checked"</c:if> /></div>
	</div>
		
	<div class="button-set">
		<button class="action" type="button" disabled="disabled" title="Update changes to core data">Update</button>
		<button class="reset" type="button" disabled="disabled" ${_resetHelp}>Reset form</button>
	</div>
</form>
	