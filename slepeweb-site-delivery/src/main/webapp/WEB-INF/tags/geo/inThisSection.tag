<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div class="in-this-section">
	<h4>In this section</h4>
		<c:forEach items="${_item.boundPages}" var="i">
			<div><a href="${i.path}">${i.fields.title}</a></div>
		</c:forEach>
</div>
