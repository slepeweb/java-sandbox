<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/languageSwitcher.tag --></gen:debug>

<!-- Now cheekily incorporating a breadcrumbs trail !! -->

<div id="header-center">

	<select id="history-selector" name="history" title="Breadcrumb trail - most recent visits">
		<option value="unset" selected>Recent picks ...</option>
		<c:forEach items="${_history}" var="_idf">
			<option value="${_idf.path}">${_idf.name}</option>
		</c:forEach>
	</select>
	
	<c:if test="${_item.site.multilingual}">
		<div id="language-selector" title="Switch language">
			<div class="welcome">
				<c:set var="_path" value="${_item.path}" />
				<c:if test="${_path eq '/'}"><c:set var="_path" value="" /></c:if>
				<c:forEach items="${_site.allLanguages}" var="lang">
					<a href="/${lang}${_path}"><img data-lang="${lang}" <c:if test="${lang eq _item.language}">class="selected"</c:if> src="/resources/anc/flag/${lang}.png" /></a>
				</c:forEach>
			</div>
		</div>	
	</c:if>
</div>
