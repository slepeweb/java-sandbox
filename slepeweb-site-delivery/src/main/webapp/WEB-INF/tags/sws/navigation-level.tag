<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="list" required="true" rtexprvalue="true" type="java.util.List" %><%@ 
	attribute name="level" required="false" rtexprvalue="true" type="java.lang.Integer" %>

<gen:debug><!-- tags/sws/navigation-level.tag --></gen:debug>

<c:choose><c:when test="${empty level}">
	<c:set var="level" value="${0}" />
	<c:set var="style" value="link-list" />
</c:when><c:otherwise>
	<c:set var="style" value="level-${level}" />
</c:otherwise></c:choose>
	
<ul class="${style}">
	<c:forEach items="${list}" var="link">
		<c:set var="clazz" value="unselected" />
		<c:if test="${link.selected}"><c:set var="clazz" value="selected" /></c:if>
		<li class="${clazz}"><a href="${link.href}">${link.title}</a>
			<c:if test="${link.selected and fn:length(link.children) > 0}">
				<sw:navigation-level list="${link.children}" level="${level + 1}" />
			</c:if>
		</li>
	</c:forEach>
</ul>
