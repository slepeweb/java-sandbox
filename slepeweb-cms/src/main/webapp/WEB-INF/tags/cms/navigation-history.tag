<%@ tag %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<select id="history-selector">
	<c:forEach items="${_history}" var="_iid">
		<option value="${_iid.itemId}">${_iid.name}</option>
	</c:forEach>
</select>
