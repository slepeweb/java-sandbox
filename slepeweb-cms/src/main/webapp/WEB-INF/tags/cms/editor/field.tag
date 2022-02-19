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
						<c:if test="${not empty fes.validator}">
							<span class="field-guidance-icon"><i class="far fa-question-circle"></i></span>
						</c:if>
						
					</div>
					
					<c:if test="${not empty fes.validator and _lang eq editingItem.language}">
						<div class="hide" data-variable="${fes.field.variable}">
							<c:if test="${not empty fes.validator.heading}">
								<h2>${fes.validator.heading}</h2></c:if>
							<c:if test="${not empty fes.validator.teaser}">
								<p>${fes.validator.teaser}</p></c:if>
							<c:if test="${not empty fes.validator.format}">
								<h3>Format</h3><p>${fes.validator.format}</p></c:if>
							
							<c:if test="${not empty fes.validator.examples}">
								<h3>Examples</h3><table>
								<c:forEach items="${fes.validator.examples}" var="_ex">
									<tr><td>${_ex.example}</td><td>${_ex.explanation}</td></tr>
								</c:forEach>
								</table>
							</c:if>

							<c:if test="${not empty fes.validator.details}">
								<h3>Details</h3><ul>
								<c:forEach items="${fes.validator.details}" var="_detail">
									<li>${_detail}</li>
								</c:forEach>
								</ul>
							</c:if>							
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
	