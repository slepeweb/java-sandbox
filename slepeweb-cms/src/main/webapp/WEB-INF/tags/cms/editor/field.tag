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
		<div id="form-fields-${_lang}" class="hideable fieldset">
			<c:forEach items="${_fieldSupport[_lang]}" var="fes">
				<c:choose><c:when test="${fes.field.type == 'layout'}">
					<hr />
				</c:when><c:otherwise>
					<div id="${fes.field.variable}" class="ff<c:if test='${fes.field.markup}'> markup</c:if>">
						<label>${fes.label} : </label>
						<div class="inputs">
							${fes.inputTag}
						</div>
						<div class="extras">
							<c:if test="${not empty fes.guidance}">
								<span class="field-guidance-icon"><i class="far fa-question-circle"></i></span>
							</c:if>
							
							<c:if test="${fes.field.expandable}">
								<div id="widefield-open-icon"><i class="fa-solid fa-arrows-left-right-to-line"></i></div>
							</c:if>							
						</div>
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
		<button class="reset" type="button" disabled="disabled" ${_resetHelp}>Reset form</button>
	</div>
</form>
	