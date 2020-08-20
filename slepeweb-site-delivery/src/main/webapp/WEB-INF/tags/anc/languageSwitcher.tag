<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/languageSwitcher.tag --></gen:debug>

<!-- Now incorporating a breadcrumbs trail !! -->

<div id="header-center">

	<select id="history-selector" name="history" title="Breadcrumb trail - most recent visits">
		<option value="unset" selected>Recent picks ...</option>
		<c:forEach items="${_history}" var="_idf">
			<option value="${_idf.path}">${_idf.name}</option>
		</c:forEach>
	</select>
	
	<c:if test="${_item.site.multilingual}">
		<div id="language-selector" title="Switch language">
	<%-- 		<c:if test="${not empty _user}"><div class="welcome">Welcome ${_user.alias}</div></c:if> --%>
			<div class="welcome">
				<c:forEach items="${_site.allLanguages}" var="lang">
					<a href="/${lang}${_item.path}"><img data-lang="${lang}" <c:if test="${lang eq _item.language}">class="selected"</c:if> src="/resources/anc/flag/${lang}.png" /></a>
				</c:forEach>
			</div>
	<%-- 		<c:if test="${not empty _user}"><div><a href="/${_item.language}/login?logout">Logout</a></div></c:if> --%>
		</div>	
	</c:if>
</div>
