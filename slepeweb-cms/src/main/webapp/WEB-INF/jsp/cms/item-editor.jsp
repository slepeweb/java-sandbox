<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<ul>
	<li><a href="#core-tab">Core</a></li>
	<li><a href="#field-tab">Fields</a></li>
	<li><a href="#inline-tab">Inlines</a></li>
	<li><a href="#relation-tab">Relations</a></li>
	<li><a href="#add-tab">Add new</a></li>
</ul>

<div id="core-tab">
	<form>
		<div>
			<label for="id">Id: </label><input disabled="disabled" value="${requestItem.id}" />
		</div>
		<div>
			<label for="path">Path: </label><input disabled="disabled" value="${requestItem.path}" />
		</div>
		<div>
			<label for="type">Type: </label><input disabled="disabled" value="${requestItem.type.name}" />
		</div>
		<div>
		<c:choose><c:when test="${not empty requestItem.template}"><c:set var="templateName" 
			value="${requestItem.template.name}" /></c:when><c:otherwise><c:set 
				var="templateName" value="No template" /></c:otherwise></c:choose>
			<label for="type">Template: </label><input disabled="disabled" value="${templateName}" />
		</div>
		<div>
			<label for="dateupdated">Date last updated: </label><input disabled="disabled" value="${requestItem.dateUpdated}" />
		</div>
		<div>
			<label for="name">Name: </label><input name="name" value="${requestItem.name}" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input name="simplename" value="${requestItem.simpleName}" />
		</div>
		<div>
			<label>&nbsp;</label><button id="core-button" type="button">Update</button>
			<button id="trash-button" type="button">Delete</button>
		</div>
	</form>
</div>

<div id="field-tab">
	<form id="field-form">
	<c:forEach items="${requestItem.fieldValues}" var="fv">
		<div>
			<label for="${fv.field.variable}">${fv.field.name}: </label>
			${fv.inputTag}
		</div>
	</c:forEach>
		<div>
			<label>&nbsp;</label><button id="field-button" type="button">Update</button>
		</div>
	</form>
</div>

<div id="inline-tab">
	<c:choose><c:when test="${fn:length(requestItem.inlineItems) > 0}">
		<ol>
		<c:forEach items="${requestItem.inlineItems}" var="item">
			<li><a href="/cms/editor/${item.id}">${item}</a></li>
		</c:forEach>
		</ol>
	</c:when><c:otherwise>
		<p>No inlines for this item</p>
	</c:otherwise></c:choose>
</div>

<div id="relation-tab">
	<c:choose><c:when test="${fn:length(requestItem.relatedItems) > 0}">
		<ol>
		<c:forEach items="${requestItem.relatedItems}" var="item">
			<li><a href="/cms/editor/${item.id}">${item}</a></li>
		</c:forEach>
		</ol>
	</c:when><c:otherwise>
		<p>No relations for this item</p>
	</c:otherwise></c:choose>
</div>

<div id="add-tab">
	<form>
		<div>
			<label for="type">Template: </label>
			<select name="template">
				<option value="0">Choose ...</option>
				<c:forEach items="${site.availableTemplates}" var="template">
					<option value="${template.id}">${template.name}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<label for="type">Type: </label>
			<select name="itemtype">
				<option value="0">Choose ...</option>
				<c:forEach items="${site.availableItemTypes}" var="it">
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