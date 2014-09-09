<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<h3>${_comp.heading}</h3>
<div>This is a ${_comp.view} feature. ${_comp.blurb}</div>
<ul class="link-list">
<c:forEach items="${_comp.targets}" var="link" end="3">
	<li class="compact"><a href="${link.href}" class="iframe group2 cboxElement">${link.title}</a>
</c:forEach>
</ul>