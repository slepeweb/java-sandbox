<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="list" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/pho/captionTagList.tag --></gen:debug>

<p class="smaller-taglist">Tags: <c:forTokens 
	items="${list}" 
	var="tag" 
	delims=", " 
	varStatus="status"><a 
		class="tag-link" 
		data-value="${tag}" 
		href="/searchresults?view=get&searchtext=${tag}">${tag}</a><c:if test="${not status.last}">, </c:if></c:forTokens></p>
