<%@ tag %><%@ 
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="sw" tagdir="/WEB-INF/tags/sws"%>

<c:forEach items="${_page.header.javascripts}" var="src">
	<script src="${src}"></script>
</c:forEach>