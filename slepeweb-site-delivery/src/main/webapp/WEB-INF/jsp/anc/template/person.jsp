<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	.passport-photo {
		margin-right: 1em;
		margin-bottom: 0.2em;
		width: 200px;
	}
	
	<anc:personMenuStyle/>
</c:set>

<anc:standardLayout>
	<gen:debug><!-- jsp/anc/person.jsp --></gen:debug>
	
	<div class="col-1-3 primary-col">	
		<anc:hierarchySvg />
	</div>
		
	<div class="col-2-3 primary-col">
		<anc:standardPerson />	
	</div>
	
</anc:standardLayout>