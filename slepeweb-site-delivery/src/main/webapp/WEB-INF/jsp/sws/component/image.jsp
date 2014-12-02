<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<!-- jsp/sws/component/image.jsp -->

<div>
	<img src="${_comp.src}" alt="${_comp.alt}" />
	<p class="img-caption tight-lines-1" style="margin-left: 5px">${_comp.caption}</p>
</div>
