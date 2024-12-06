<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div class="in-this-section">
	<h4>In this section 
		<span style="padding-left: 2em;"><a href="${_inThisSection.root.href}" 
			title="Navigate upwards"><i class="fa-solid fa-arrow-up"></i></a></span></h4>
			
	<c:forEach items="${_inThisSection.root.children}" var="sib_target">
		<div<c:if test="${sib_target.selected}"> class="selected"</c:if>>${sib_target.tag}
			<c:if test="${sib_target.selected}">
				<c:forEach items="${sib_target.children}" var="c_target">
					<div class="selected-child">${c_target.tag}</div>
				</c:forEach>
			</c:if>
		</div>
	</c:forEach>
</div>
