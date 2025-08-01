<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/component/table.jsp --></gen:debug>

<c:set var="clazz" value="table" />
<c:if test="${not empty _comp.cssClass}"><c:set var="clazz" value="${clazz} ${_comp.cssClass}" /></c:if>

<table id="component-${_comp.enumerator}" class="${clazz}">

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
