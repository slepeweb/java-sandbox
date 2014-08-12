<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<ul>
	<li><a href="#field-tab">Fields</a></li>
	<li><a href="#core-tab">Core</a></li>
	<li><a href="#inline-tab">Inlines</a></li>
	<li><a href="#relation-tab">Relations</a></li>
</ul>

<div id="core-tab">
	<form action="/rest/cms/item/${requestItem.id}/update/core">
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
			<label for="dateupdated">Date last updated: </label><input disabled="disabled" value="${requestItem.dateUpdated}" />
		</div>
		<div>
			<label for="name">Name: </label><input name="name" value="${requestItem.name}" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input name="simplename" value="${requestItem.simpleName}" />
		</div>
		<div>
			<label>&nbsp;</label><button id="core-button" type="button">Submit</button>
		</div>
	</form>
</div>

<div id="field-tab">
	<form id="field-form" action="/rest/cms/item/${requestItem.id}/update/fields">
	<c:forEach items="${requestItem.fieldValues}" var="fv">
		<div>
			<label for="${fv.field.variable}">${fv.field.name}: </label>
			${fv.inputTag}
		</div>
	</c:forEach>
		<div>
			<label>&nbsp;</label><button id="field-button" type="button">Submit</button>
		</div>
	</form>
</div>

<div id="inline-tab">
</div>

<div id="relation-tab">
</div>
