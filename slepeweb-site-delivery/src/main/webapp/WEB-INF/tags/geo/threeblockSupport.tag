<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<%--
	A three-block component has 3 cells on one row, 'left', 'middle' and 'right'. It extends
	the notion of a two-block component (see twoblock.jsp for more info).
--%>

<c:set var="clazz" value="threeblock nblock" />
<c:if test="${not empty _comp.cssClass}"><c:set var="clazz" value="${clazz} ${_comp.cssClass}" /></c:if>

<c:set var="numSubComponents" value="${fn:length(_comp.components)}" />
<c:set var="numSubComponentsUsed" value="0" />

<c:set var="left" value="${_comp.left}" scope="request" />
<c:if test="${empty left and numSubComponents > 0}">
	<c:set var="left" scope="request">
		<site:insertComponent site="${_item.site.shortname}" component="${_comp.components[0]}" />
		<c:set var="numSubComponentsUsed" value="1" />
	</c:set>
</c:if>

<c:set var="middle" value="${_comp.middle}" scope="request" />
<c:if test="${empty middle and numSubComponents > numSubComponentsUsed}">
	<c:set var="middle" scope="request">
		<site:insertComponent site="${_item.site.shortname}" component="${_comp.components[numSubComponentsUsed]}" />
		<c:set var="numSubComponentsUsed" value="${numSubComponentsUsed + 1}" />
	</c:set>
</c:if>

<c:set var="right" value="${_comp.right}" scope="request" />
<c:if test="${empty right and numSubComponents > numSubComponentsUsed}">
	<c:set var="right" scope="request">
		<site:insertComponent site="${_item.site.shortname}" component="${_comp.components[numSubComponentsUsed]}" />
	</c:set>
</c:if>

<div id="component-${_comp.enumerator}" class="${clazz}">
	<jsp:doBody />
</div>
