<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_assembly">
	<div>${_item.fields.bodytext}</div>
	<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" view="${_item.requestPack.view}" />
</c:set>

<c:choose><c:when test="${_item.requestPack.view eq 'pdf'}">
	${site:transformMagicMarkup4Pdf(_assembly, _localHostname, _magicMarkupService)}
</c:when><c:otherwise>
	${site:transformMagicMarkup(_assembly, _magicMarkupService)}
</c:otherwise></c:choose>