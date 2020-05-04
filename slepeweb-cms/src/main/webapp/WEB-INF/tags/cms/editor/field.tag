<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/field.tag --></cms:debug>
	
<form id="field-form" enctype="multipart/form-data" accept-charset="utf-8">

	<c:if test="${editingItem.site.multilingual}">
		<div id="field-language-selector" class="ff">
			<label>Language : </label>
			<select name="language">
				<c:forEach items="${editingItem.site.allLanguages}" var="_lang">
					<option value="${_lang}" <c:if 
						test="${editingItem.language eq _lang}">selected</c:if>>${_lang}</option>
				</c:forEach>
			</select>
		</div>
	</c:if>
	
	<c:forEach items="${editingItem.site.allLanguages}" var="_lang">
		<div id="form-fields-${_lang}" class="hideable">
			<c:forEach items="${_fieldSupport[_lang]}" var="fes">
			<!-- fes.field.type == [${fes.field.type}] -->
				<c:choose><c:when test="${fes.field.type == 'layout'}">
					<hr />
				</c:when><c:otherwise>
					<div class="ff">
						<label for="${fes.field.variable}">${fes.label} : </label>
						${fes.inputTag}
					</div>
				</c:otherwise></c:choose>
			</c:forEach>
		</div>
	</c:forEach>
	
	<div>
		<button id="field-button" type="button">Update</button>
	</div>
</form>
	