<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<%-- <c:set var="_extraJs" scope="request">/resources/geo/js/homepage.js</c:set> --%>

<geo:pageLayout type="std">
	<gen:debug><!-- jsp/geo/homepage.jsp --></gen:debug>
		
	<div class="main home">
		<h2>${_item.fields.title}</h2>
	</div>
</geo:pageLayout>