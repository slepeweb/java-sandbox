<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<%-- This jsp is used for both Gallery and Records pages --%>
		
<c:set var="_extraInpageCss" scope="request">
	<anc:personMenuStyle/>
	<anc:personSubMenuStyle/>
</c:set>

<anc:pageLayout type="leftmenu">
	<gen:debug><!-- jsp/anc/record.jsp --></gen:debug>
	
	<div class="menu">
		<anc:personMenu />
	</div>
	
	<div class="main">
		<c:choose><c:when test="${_target.type == 'PDF'}">
			<h2>${_target.fields.title}</h2>
			<p>${_target.fields.teaser}</p>
			
			<object data="${_target.url}" type="application/pdf" width="100%" height="800px">
				This browser does not support PDFs.
			</object> 
		</c:when><c:when test="${fn:startsWith(_target.type, 'Image')}">
			<h2>${_target.fields.alt}</h2>
			<p>${_target.fields.caption}</p>
			<img src="${_target.url}" width="100%" />
		</c:when></c:choose>
	</div>
	
	<div class="leftside">
		<anc:personSubMenu />
	</div>
	
</anc:pageLayout>