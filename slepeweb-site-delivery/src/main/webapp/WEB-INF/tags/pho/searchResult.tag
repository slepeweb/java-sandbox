<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="result" type="com.slepeweb.cms.bean.SolrDocument4Cms" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/pho/searchResult.tag --></gen:debug>

<c:set var="_forbiddenSrc" value="/resources/pho/images/forbidden.png" scope="request" />
<c:set var="_src" value="${result.path}" scope="request" />
<c:set var="_heading" value="${result.title}" scope="request" />
<c:set var="_teaser" value="${result.teaser}" scope="request" />
<c:set var="_dateish" value="${site:toDateish(result.extraStr1)}" scope="request" />		

<c:if test="${not result.accessible}">
	<c:set var="_src" value="${_forbiddenSrc}" scope="request" />
	<c:set var="_heading" value="No access" scope="request" />
	<c:set var="_teaser" value="" scope="request" />
</c:if>

<c:if test="${not empty _dateish.year}"><c:set var="_teaser" scope="request">${result.teaser} (${_dateish.deliveryString})</c:set></c:if>
