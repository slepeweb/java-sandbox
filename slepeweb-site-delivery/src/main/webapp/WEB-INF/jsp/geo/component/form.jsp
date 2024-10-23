<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>

<%-- 	<c:if test="${not empty _comp.heading}"><h3>${_comp.heading}</h3></c:if> --%>
<%-- 	<c:if test="${not empty _comp.body}"><div>${_comp.body}</div></c:if> --%>
	
	<table<c:if test="${not empty _comp.cssClass}"> class="${_comp.cssClass}"</c:if>>
		<tr>
			<c:forEach items="${_comp.columnHeadings}" var="col">
				<th<c:if test="${col.width gt 0}"> style="width: ${col.width}%"</c:if>>${col.label}</th>
			</c:forEach>
		</tr>
		
		<c:forEach items="${_comp.rows}" var="row">
			<tr>
				<c:forEach items="${row.cells}" var="cell">
					<td>${cell}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
</div>
