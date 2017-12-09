<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<c:set var="_showVersionTab" value="${editingItem.type.name ne 'ContentFolder' and (editingItem.published or editingItem.version > 1)}" />

<ul id="editor-tabs">
	<li><a href="#core-tab">Core</a></li>
	<li><a href="#field-tab">Fields</a></li>
	<li><a href="#links-tab">Links</a></li>
	<c:if test="${editingItem.type.media}">
		<li><a href="#media-tab">Media</a></li>
	</c:if>
	<li><a href="#add-tab">Add new</a></li>
	<c:if test="${editingItem.path ne '/'}">
		<li><a href="#copy-tab">Copy</a></li>
	</c:if>
	<c:if test="${_showVersionTab}">
		<li><a href="#version-tab">Version</a></li>
	</c:if>
		<li><a href="#misc-tab">Misc</a></li>
</ul>

<div id="core-tab">
	<form>
		<%-- Javascript looks for this input element --%>
		<input id="itemIsProductFlag" type="hidden" value="${editingItem.product}" />
		
		<div>
			<label for="id">Id: </label><input disabled="disabled" value="${editingItem.id}" />
		</div>
		<div>
			<label for="path">Path: </label><input disabled="disabled" value="${editingItem.path}" />
			<a href="${editingItem.path}" target="_blank">View</a>
		</div>
		<div>
			<label for="type">Type: </label><input disabled="disabled" value="${editingItem.type.name}" />
		</div>
		<div>
			<label for="dateupdated">Date last updated: </label><input disabled="disabled" value="${editingItem.dateUpdated}" />
		</div>
		<div>
			<label for="version">Version: </label><input disabled="disabled" value="${editingItem.version}" />
		</div>
		<div>
			<label for="type">Template: </label>
			<select name="template">
				<option value="0">Choose ...</option>
				<c:forEach items="${availableTemplatesForType}" var="template">
					<option value="${template.id}"<c:if 
						test="${not empty editingItem.template and 
							template.id eq editingItem.template.id}"> selected</c:if>>${template.name}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<label for="name">Name: </label><input type="text" name="name" value="${editingItem.name}" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input type="text" name="simplename" value="${editingItem.simpleName}" <c:if 
				test="${editingItem.path eq '/'}">disabled="disabled"</c:if> />
		</div>
		
		<%-- This div will only be visible if the selected item type is Product --%>
		<c:if test="${editingItem.product}">
			<div>
				<label for="partNum">Part number: </label><input type="text" name="partNum" value="${editingItem.partNum}" />
			</div>
			<div>
				<label for="price">Price: </label><input type="text" name="price" value="${editingItem.priceInPoundsAsString}" />
			</div>
			<div>
				<label for="stock">Stock: </label><input type="text" name="stock" value="${editingItem.stock}" />
			</div>
			
			<c:choose><c:when test="${not editingItem.hasVariants}">
				<div>
					<label for="alphaaxis">Axis A: </label>
					<select name="alphaaxis">
						<option value="-1">Choose ...</option>
						<c:forEach items="${availableAxes}" var="axis">
							<option value="${axis.id}"<c:if 
								test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
						</c:forEach>
					</select>
				</div>
				<div>
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
				<div>
					<label>Axis A: </label>
					<input disabled="disabled" value="${editingItem.alphaAxis.shortname}" />
				</div>
				<div>
					<label>Axis B: </label>
					<input disabled="disabled" value="${editingItem.betaAxis.shortname}" />
				</div>
			</c:otherwise></c:choose>
		</c:if>
		
		<div>
			<label for="tags">Tags: </label><input type="text" name="tags" value="${editingItem.tagsAsString}" />
		</div>
		<div>
			<label for="searchable">Searchable? </label><input type="checkbox" name="searchable" <c:if test="${editingItem.searchable}">checked="checked"</c:if> />
		</div>
		<div>
			<label for="published">Published? </label><input type="checkbox" name="published" <c:if test="${editingItem.published}">checked="checked"</c:if> />
		</div>
		<div>
			<label>&nbsp;</label><button id="core-button" type="button">Update</button>
			<button id="trash-button" type="button">Delete</button>
		</div>
	</form>
</div>

<script>
	$(function() {
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
	});
</script>

<div id="field-tab">
	<form id="field-form">
	<c:set var="fvm" value="${editingItem.fieldValuesMap}" />
	<c:forEach items="${editingItem.type.fieldsForType}" var="fft">
		<c:set var="fv" value="${fvm[fft.field.variable]}" />
		<c:choose>
			<c:when test="${empty fv}">
				<c:set var="variable" value="${fft.field.variable}" />
				<c:set var="label" value="${fft.field.name}" />
				<c:set var="inputTag" value="${fft.field.inputTag}" />
			</c:when>
			<c:otherwise>
				<c:set var="fv" value="${fvm[fft.field.variable]}" />
				<c:set var="variable" value="${fv.field.variable}" />
				<c:set var="label" value="${fv.field.name}" />
				<c:set var="inputTag" value="${fv.inputTag}" />
			</c:otherwise>
		</c:choose>
		<div>
			<label for="${variable}">${label} : </label>
			${inputTag}
		</div>
	</c:forEach>
		<div>
			<label>&nbsp;</label><button id="field-button" type="button">Update</button>
		</div>
	</form>
</div>

<div id="links-tab">
	<div>
		<ul id="sortable-links">
			<c:forEach items="${editingItem.allLinksBarBindings}" var="link">
				<li class="sortable-link ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><a 
					href="${applicationContextPath}/page/editor/${link.child.id}">${link}</a>
						<button class="remove-link float-right">Remove</button><span 
							class="hide">${link.parentId},${link.child.id},${link.type},${link.name}</span></li>
			</c:forEach>
		</ul>
		<div class="spacer20">
			<button id="addlink-button" type="button">Add link</button>
			<button id="savelinks-button" type="button">Save changes</button>
		</div>
	</div>
	
	<div class="spacer20"></div>
	<div id="addlinkdiv">
		<div class="float-left">
			<div>
				<label for="linktype">Link type: </label>
				<select name="linktype">
					<option value="unknown">Choose ...</option>
					<c:forTokens items="inline,relation,component,shortcut" delims="," var="type">
						<option value="${type}">${type}</option>
					</c:forTokens>
				</select>	
			</div>
			<div>
				<label for="linkname">Link name: </label>
				<select name="linkname">
					<option value="unknown">Choose ...</option>
				</select>	
			</div>
		</div>		
		<div id="linknav" class="inline"></div>
	</div>
	
	<ul id="link-template" class="hide">
		<li class="ui-state-default">
			<span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
			<a href="*">*</a>
			<button class="remove-link float-right">Remove</button>
			<span class="hide">*</span>
		</li>		
	</ul>
</div>

<c:if test="${editingItem.type.media}">
	<div id="media-tab">
		<form id="media-form" enctype="multipart/form-data">
			<div>
				<label for="media">&nbsp;</label>
				<input name="media" type="file" />
			</div>
			<div>
				<label>&nbsp;</label><button id="media-button" type="button">Update media</button>
			</div>
			<label>&nbsp;</label><progress class="spacer10"></progress>		
		</form>
	</div>
</c:if>

<div id="add-tab">
	<form>
		<div>
			<label for="type">Template: </label>
			<select name="template">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableTemplates}" var="template">
					<option value="${template.id}" 
						data-isproduct="${template.itemTypeId == _productTypeId ? 1 : 0}">${template.name}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<label for="type">Type: </label>
			<select name="itemtype">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableItemTypes}" var="it">
					<option value="${it.id}"
						data-isproduct="${it.id == _productTypeId ? 1 : 0}">${it.name}</option>
				</c:forEach>
			</select>			
		</div>
		<div>
			<label for="name">Name: </label><input name="name" value="" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input name="simplename" value="" />
		</div>
		
		<%-- This div will only be visible if the selected item type is Product --%>
		<div id="core-commerce">
			<div>
				<label for="partNum">Part number: </label><input type="text" name="partNum" value="" />
			</div>
			<div>
				<label for="price">Price: </label><input type="text" name="price" value="0" />
			</div>
			<div>
				<label for="stock">Stock: </label><input type="text" name="stock" value="0" />
			</div>
			<div>
				<label for="alphaaxis">Axis A: </label>
				<select id="alphaaxis" name="alphaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
			<div>
				<label for="betaaxis">Axis B: </label>
				<select id="betaaxis" name="betaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.betaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div>
			<label>&nbsp;</label><button id="add-button" type="button">Add</button>
		</div>
	</form>
</div>

<c:if test="${editingItem.path ne '/'}">
	<%-- Avoid calling the getCopyDetails() method more than once per request --%>
	<c:set var="_copyDetails" value="${editingItem.copyDetails}" />
	<div id="copy-tab">
		<form>
			<div>
				<label for="name">Name: </label><input name="name" value="${_copyDetails[2]}" />
			</div>
			<div>
				<label for="simplename">Simple name: </label><input name="simplename" 
					value="${_copyDetails[1]}" />
			</div>
			<div>
				<label>&nbsp;</label><button id="copy-button" type="button">Copy</button>
			</div>
		</form>
	</div>
</c:if>

<c:if test="${_showVersionTab}">
	<div id="version-tab">
		<form>
			<table width="100%">
				<tr>
					<c:choose><c:when test="${editingItem.published}">
						<td><label>Click button to create a new version: </label></td>
						<td><button id="version-button" type="button">Version</button></td>
					</c:when><c:otherwise>
						<td><label>Cannot version an un-published item: </label></td>
						<td><button id="version-button-disabled" class="disabled" type="button">Version</button></td>
					</c:otherwise></c:choose>
				</tr>
				
				<c:if test="${editingItem.version > 1}">
					<tr>
							<td><label>Click button to revert to previous version: </label></td>
							<td><button id="revert-button" type="button">Revert</button></td>
					</tr>
				</c:if>
			</table>
		</form>
	</div>
</c:if>

<div id="misc-tab">
	<button id="trash-show-button" type="button">Show bin</button>
	<div id="trash-container"></div>
</div>
