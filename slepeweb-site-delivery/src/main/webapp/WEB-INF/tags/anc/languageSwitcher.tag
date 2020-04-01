<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/languageSwitcher.tag --></gen:debug>

<c:if test="${_item.site.multilingual}">
	<div id="language-selector">
		<div>
		<c:forEach items="${_site.allLanguages}" var="lang">
			<a href="/${lang}${_item.path}"><img data-lang="${lang}" <c:if test="${lang eq _item.language}">class="selected"</c:if> src="/resources/anc/flag/${lang}.png" /></a>
		</c:forEach>
		</div>
	</div>	
</c:if>