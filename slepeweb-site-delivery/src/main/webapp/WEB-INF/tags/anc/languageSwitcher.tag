<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/languageSwitcher.tag --></gen:debug>

<div id="language-selector">
	<span>Switch language: </span>		
	<select>
		<c:forEach items="${_site.allLanguages}" var="lang">
			<option value="${lang}" <c:if test="${lang eq _item.language}">selected</c:if>>${lang}</option>
		</c:forEach>
	</select>
</div>

<script>
	$(function(){
		$("#language-selector select").change(function(){
			window.location = "/" + $(this).val() + "${_item.path}";
		});
	});
</script>