<%@ taglib uri="pageStudioTags" prefix="ps" %><%@ 
	taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<ps:component>
	<%
		pageContext.setAttribute("_heading", propertyPublisher.getProperty("heading"));
		pageContext.setAttribute("_max", Integer.parseInt(propertyPublisher.getProperty("max")));
	%>
 	<h2>${_heading}</h2>
 	<c:if test="${not empty __genericList}">
		<c:forEach items="${__genericList}" var="item" end="${_max - 1}">
			<a href="${item.path}">${item.text}</a><br />
		</c:forEach>
		
		<c:set var="_num" value="${fn:length(__genericList)}" />
		<c:choose><c:when test="${_num > _max}">
			<p>More ...</p>
		</c:when><c:when test="${_num == 0}">
			<p>No links available</p>
		</c:when></c:choose>
	</c:if>
</ps:component>