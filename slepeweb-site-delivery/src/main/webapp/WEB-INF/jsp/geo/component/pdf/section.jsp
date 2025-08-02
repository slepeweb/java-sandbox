<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/component/section.jsp --></gen:debug>

<c:set var="clazz" value="section" />
<c:if test="${not empty _comp.cssClass}"><c:set var="clazz" value="${clazz} ${_comp.cssClass}" /></c:if>

<div id="component-${_comp.enumerator}" class="${clazz}">

	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if>
	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if>
		
</div>

<site:insertComponents site="${_item.site.shortname}" list="${_comp.components}" view="${_item.requestPack.view}" />
