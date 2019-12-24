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
	
	<!-- Left side -->	
	<div class="col-1-2 primary-col">
		<anc:standardPerson />	
	</div>
	
	<!-- Right side -->
	<div class="col-1-2 primary-col">	
		<anc:hierarchySvg />
	</div>
		
	<%--
	<script>
		$(function(){
			$("#person-tabs").tabs({active: 0});
		});
	</script>
	 --%>
</anc:standardLayout>