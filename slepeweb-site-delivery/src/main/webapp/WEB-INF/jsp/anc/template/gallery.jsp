<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<%-- This jsp is used for both Gallery and Records pages --%>
		
<c:set var="_extraCss" scope="request">
	/resources/anc/css/lightbox.css
</c:set>

<c:set var="_extraInpageCss" scope="request">
	<anc:personMenuStyle/>
</c:set>

<c:set var="_extraJs" scope="request">
	/resources/anc/js/lightbox.js
</c:set>

<anc:pageLayout type="leftmenu">
	<gen:debug><!-- jsp/anc/gallery.jsp --></gen:debug>
	
	<c:if test="${not empty _person.firstName}">
		<div class="leftside">
			<h3 class="emphasis">${_person.firstName}'s photo gallery</h3>
		</div>
	</c:if>
	
	<div class="menu">
		<anc:personMenu />
	</div>
	
	<div class="main">
		<div class="image-gallery">
			<c:forEach items="${_gallery}" var="_img">
				<a href="${_img.url}" data-lightbox="image-gallery" data-title="Help instructions"><img 
					class="example-image" src="${_img.url}?view=thumbnail" alt="*" /></a>
			</c:forEach>
		</div>
	</div>
	
</anc:pageLayout>