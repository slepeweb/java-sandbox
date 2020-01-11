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
	
	<!-- 
	<span class="icon">
		<i class="fas fa-home icon-large" style="color: teal;"></i> 
		<i class="fas fa-male icon-large" style="color: #3399ff;"></i> 
		<i class="fas fa-female icon-large" style="color: #ff9999;"></i>
		<i class="fas fa-file-pdf icon-large" style="color: #ff3333;"></i> 
		<i class="fas fa-file-alt icon-large"></i>
		<i class="fas fa-image icon-large" style="color: #009900;"></i>
	</span>
	 -->

</anc:standardLayout>