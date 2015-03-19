<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<ul>
	<li><a href="#core-tab">Core</a></li>
	<li><a href="#field-tab">Fields</a></li>
	<li><a href="#links-tab">Links</a></li>
	<c:if test="${editingItem.type.media}">
		<li><a href="#media-tab">Media</a></li>
	</c:if>
	<li><a href="#add-tab">Add new</a></li>
</ul>

<div id="core-tab">
	<form>
		<div>
			<label for="id">Id: </label><input disabled="disabled" value="${editingItem.id}" />
		</div>
		<div>
			<label for="path">Path: </label><input disabled="disabled" value="${editingItem.path}" />
		</div>
		<div>
			<label for="type">Type: </label><input disabled="disabled" value="${editingItem.type.name}" />
		</div>
		<div>
			<label for="dateupdated">Date last updated: </label><input disabled="disabled" value="${editingItem.dateUpdated}" />
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
			<label for="simplename">Simple name: </label><input type="text" name="simplename" value="${editingItem.simpleName}" />
		</div>
		<div>
			<label for="tags">Tags: </label><input type="text" name="tags" value="${editingItem.tagsAsString}" />
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
				<li class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span><a 
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
					<option value="${template.id}">${template.name}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<label for="type">Type: </label>
			<select name="itemtype">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableItemTypes}" var="it">
					<option value="${it.id}">${it.name}</option>
				</c:forEach>
			</select>			
		</div>
		<div>
			<label for="name">Name: </label><input name="name" value="" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input name="simplename" value="" />
		</div>
		<div>
			<label>&nbsp;</label><button id="add-button" type="button">Add</button>
		</div>
	</form>
</div>
