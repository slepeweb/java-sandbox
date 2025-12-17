<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:if test="${fn:length(_item.relatedItems) > 0}">
	<div class="related-items">
		<h4>Related items</h4>
			<c:forEach items="${_item.relatedItems}" var="i">
				<c:set var="_attr">${i.foreigner ? 'class="xlink"' : ''}</c:set>
				<c:set var="_path">${i.foreigner ? i.miniPath : i.path}</c:set>
				
				<div><i class="fa-regular ${i.type.fontAwesomeClass}"></i>&nbsp;<a ${_attr} href="${_path}">${i.fields.title}</a></div>
			</c:forEach>
	</div>
</c:if>