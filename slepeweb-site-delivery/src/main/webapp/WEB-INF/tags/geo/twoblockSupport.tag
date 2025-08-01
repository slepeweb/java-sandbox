<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/geo/twoblockSupport.jsp --></gen:debug>

<%--
	A two-block component has 2 cells on one row, 'left' and 'right'. The content for each of these cells
	comes from either a) a corresponding item field value, or b) a component item (ie a type of binding).
	Option a) takes precedence over option b).
--%>

<c:set var="clazz" value="twoblock nblock" />
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

<c:set var="right" value="${_comp.right}" scope="request" />
<c:if test="${empty right and numSubComponents > numSubComponentsUsed}">
	<c:set var="right" scope="request">
		<site:insertComponent site="${_item.site.shortname}" component="${_comp.components[numSubComponentsUsed]}" />
	</c:set>
</c:if>

<div id="component-${_comp.enumerator}" class="${clazz}">
	<jsp:doBody />
</div>
