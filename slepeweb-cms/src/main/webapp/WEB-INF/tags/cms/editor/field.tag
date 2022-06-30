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
				<c:choose><c:when test="${fes.field.type == 'layout'}">
					<hr />
				</c:when><c:otherwise>
					<div class="ff">
						<label for="${fes.field.variable}">${fes.label} : </label>
						${fes.inputTag}
						<c:if test="${not empty fes.guidance}">
							<span class="field-guidance-icon"><i class="far fa-question-circle"></i></span>
						</c:if>
						
					</div>
					
					<c:if test="${not empty fes.guidance and _lang eq editingItem.language}">
						<div class="hide" data-variable="${fes.field.variable}">
							<edit:guidance guidance="${fes.guidance}" />
						</div>
					</c:if>
				</c:otherwise></c:choose>
			</c:forEach>
		</div>
	</c:forEach>
	
	<div class="button-set">
		<button class="action" type="button" disabled="disabled" title="Update the changes to field values">Update</button>
		<button class="reset" type="button" disabled="disabled">Reset form</button>
	</div>
</form>
	