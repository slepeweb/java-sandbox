<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	<anc:personMenuStyle/>
	<anc:personSubMenuStyle/>
</c:set>

<anc:standardLayout>
	<gen:debug><!-- jsp/anc/record.jsp --></gen:debug>
	
	<div class="col-3-4 primary-col pull-right-sm">
		<anc:personMenu />
		<h2>${_target.fields.title}</h2>
		<p>${_target.fields.teaser}</p>
		
		<c:choose><c:when test="${_target.type == 'PDF'}">
			<object data="${_target.path}" type="application/pdf" width="100%" height="800px">
				This browser does not support PDFs.
			</object> 
		</c:when></c:choose>
	</div>
	
	<div class="col-1-4 primary-col">
		<anc:personSubMenu />
	</div>
	
</anc:standardLayout>