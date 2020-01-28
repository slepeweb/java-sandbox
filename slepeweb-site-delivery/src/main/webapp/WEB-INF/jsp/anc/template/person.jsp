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
		<c:forEach items="${_svgList}" var="_support" varStatus="_stat">
			<anc:hierarchySvg svgdata="${_support}" index="${_stat.index}" />
		</c:forEach>
			
		<c:if test="${_person.multiPartnered}">
			<div id="partner-options">
				${_person.firstName}'s partners: <br />
				<c:forEach items="${_person.relationships}" var="_rel" varStatus="_stat">
					<span><input type="radio" name="svgindex" value="${_stat.index}" 
						<c:if test="${_stat.index eq 0}">checked</c:if>/> ${_rel.partner.name} </span>
				</c:forEach>
			</div>
		</c:if>
	</div>
		
	<div class="col-2-3 primary-col">
		<anc:standardPerson />	
	</div>
	
	<script>
		var switchHierarchy = function(target) {
			$(".hierarchy-diagram").each(function(idx){
				if (idx != target) {
					$(this).hide();
				}
				else {
					$(this).show();
				}
			});
		};
		
		$(function(){
			switchHierarchy(0);
			
			$("#partner-options input").change(function(){
				var index = $("#partner-options input:checked").val();
				switchHierarchy(index);
			});
		});
	</script>
	
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