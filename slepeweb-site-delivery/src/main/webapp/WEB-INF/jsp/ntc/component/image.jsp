<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<!-- jsp/ntc/component/image.jsp -->

<div>
	<img src="${_comp.src}" alt="${_comp.alt}" width="100%" <c:if test="${not empty _comp.maxWidth}"> 
		style="max-width: ${_comp.maxWidth}px"</c:if> />
	<c:if test="${not empty _comp.caption}"><p class="caption" style="margin-left: 5px">${_comp.caption}</p></c:if>
</div>
