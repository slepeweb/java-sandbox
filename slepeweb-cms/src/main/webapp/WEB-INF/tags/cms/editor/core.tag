<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/core.tag --></cms:debug>
	
<div id="core-tab">
	<form>
		<%-- Javascript looks for this input element --%>
		<input id="itemIsProductFlag" type="hidden" value="${editingItem.product}" />
		
		<div>
			<label for="id">Id: </label><input disabled="disabled" value="${editingItem.id}" />
		</div>
		<div>
			<label for="path">Path: </label><input disabled="disabled" value="${editingItem.path}" />
			<c:set var="url" value="${editingItem.path}" />
			<c:if test="${editingItem.site.multilingual}"><c:set var="url" value="/${editingItem.site.language}${url}" /></c:if>
			<a href="${url}" target="_blank">View</a>
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
				test="${editingItem.root}">disabled="disabled"</c:if> />
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

	