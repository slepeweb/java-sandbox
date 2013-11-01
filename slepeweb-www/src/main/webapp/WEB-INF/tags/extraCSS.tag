<%@ tag %><%@ 
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="sw" tagdir="/WEB-INF/tags"%>

<c:forEach items="${_page.header.stylesheets}" var="href">
	<link rel="stylesheet" href="${href}" type="text/css">
</c:forEach>