<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- 
	This navigation code only supports 2 link levels. 
	Changes required if we need to accommodate 3 levels 
--%>

<section>
	<ul class="link-list">
	<c:forEach items="${_page.leftSidebar.navigation}" var="link">
		<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a>
		<c:if test="${link.selected and fn:length(link.children) > 0}">
			<ul class="level-1">
			<c:forEach items="${link.children}" var="sublink">
				<li<c:if test="${sublink.selected}"> class="selected"</c:if>><a href="${sublink.href}">${sublink.title}</a></li>
			</c:forEach>
			</ul>
		</c:if>
		</li>
	</c:forEach>
	</ul>
</section>
