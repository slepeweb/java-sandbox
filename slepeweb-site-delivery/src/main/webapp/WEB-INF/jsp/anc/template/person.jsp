<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	.passport-photo {
		margin-right: 1em;
		margin-bottom: 0.2em;
		width: 200px;
	}
	
	#partner-options {
		margin-top: 0.8em;
		font-size: 0.8em;
	}
	
	#partner-options span {
		margin-left: 0.5em;
		margin-right: 1.0em;
		display: inline;
	}
	
	#partner-options input {
		vertical-align: middle;
	}
	<anc:personMenuStyle/>
</c:set>

<anc:standardLayout>
	<gen:debug><!-- jsp/anc/person.jsp --></gen:debug>
	
	<div class="col-1-3 primary-col">	
		<anc:hierarchySvg />
		
		<c:if test="${_person.multiPartnered}">
			<div id="partner-options">
				${_person.firstName}'s partners: 
				<c:forEach items="${_person.relationships}" var="_rel">
					<span><input type="radio" name="x" value="${_rel.partner.item.id}" /> ${_rel.partner.name} </span>
				</c:forEach>
			</div>
			
			<script>
				$(function(){
					$("#partner-options input").change(function(ele){
						console.log("Switching diagram to #" + $(this).val());
					});
				});
			</script>
		</c:if>
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