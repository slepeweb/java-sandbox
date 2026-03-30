<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:if test="${fn:length(_page.stdRelatedLinkTargets) > 0}">
	<div class="related-items">
		<h4>Related items</h4>
			<c:forEach items="${_page.stdRelatedLinkTargets}" var="_target">
				<c:set var="_attr">${_target.foreigner ? 'class="xlink"' : ''}</c:set>				
				<div><i class="fa-regular ${_target.fontAwesomeClass}"></i>&nbsp;<a 
					target="_blank" ${_attr} href="${_target.href}">${_target.title}</a></div>
			</c:forEach>
	</div>
</c:if>