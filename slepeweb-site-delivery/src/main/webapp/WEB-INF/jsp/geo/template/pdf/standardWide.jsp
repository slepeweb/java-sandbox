<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<h2 id="page-title">${_item.fields.title}</h2>
<div>${site:parseXimg(_item.fields.bodytext, _ximgService, _passkey)}</div>

<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />
