<%@ tag %><%@ 
	attribute name="link" required="true" rtexprvalue="true" type="com.slepeweb.cms.bean.solr.SolrPageLink" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/solr-page-link.tag --></gen:debug>

<c:choose><c:when test="${link.selected}">
	<div class="solr-page selected">${link.label}</div>
</c:when><c:otherwise>
	<div class="solr-page"><a href="${link.href}">${link.label}</a></div>
</c:otherwise></c:choose>
