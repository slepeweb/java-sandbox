<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/fields.tag --></cms:debug>
	
<div id="field-tab">
	<form id="field-form" enctype="multipart/form-data" accept-charset="utf-8">
		<c:set var="fvm" value="${editingItem.fieldValues}" />
		
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
</div>

<script>
	var _siteDefaultLanguage = "${editingItem.site.language}";

	var _toggleFieldDivs = function(lang) {
		$(".hideable").each(function() {
			var ele = $(this);
			if (ele.attr("id").endsWith(lang)) {
				ele.show();
			}
			else {
				ele.hide();
			}
		});
	}
	
	$(function() {
		var language = localStorage.getItem("language");
		if (! language) {
			language = _siteDefaultLanguage;
		}
		
		$("#field-language-selector select").val(language);
		_toggleFieldDivs(language);
		
		$("#field-language-selector select").change(function(){
			var lang = $(this).val();
			localStorage.setItem("language", lang);
			_toggleFieldDivs(lang);
		});
	});
</script>
	