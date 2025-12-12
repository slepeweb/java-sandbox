<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<pho:collageLayout>
	
		<c:forEach items="${_photoList}" var="_row" varStatus="_stat">
			<c:set var="_margin" value="${_photoMargin[_stat.index]}" />
			<c:set var="_rowSize" value="${fn:length(_row)}" />

			<div class="photo-row">
				<c:forEach items="${_row}" var="_id" varStatus="_imgstat">
					<c:set var="_style">style="margin-left: ${_margin}px;"</c:set>
				
					<!-- _imgstat.index = ${_imgstat.index} -->
					<c:if test="${_imgstat.index gt 0}"><c:set var="_style" value="" /></c:if>
					<c:set var="_src" value="/$_${_id}" />
					<c:if test="${_id eq 0}"><c:set var="_src" value="/resources/pho/happy40th.jpg" /></c:if>
					<img id="${_id}" src="${_src}" ${_style} />
				</c:forEach>
			</div>
		</c:forEach>
				
</pho:collageLayout>