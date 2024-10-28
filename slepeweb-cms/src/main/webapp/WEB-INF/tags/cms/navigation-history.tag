<%@ tag %><%@ taglib uri="jakarta.tags.core" prefix="c"%>

<select id="history-selector">
	<c:forEach items="${_history}" var="_iid">
		<option value="${_iid.itemId}">${_iid.name}</option>
	</c:forEach>
</select>
