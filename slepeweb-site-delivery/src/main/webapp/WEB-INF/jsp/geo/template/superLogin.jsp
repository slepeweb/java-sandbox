<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/topSecret.jsp --></gen:debug>

<geo:pageLayout type="std">
		
	<div class="main standard-3col">
		<div class="leftside">
			<div class="in-this-section">
				<c:if test="${not empty param.warning}"><p class="warning">${param.warning}</p>
				</c:if>
			</div>
		</div>
		
		<div class="rightside">
			<div class="mainbody full-width">
			
				<h2 id="page-title">${_item.fields.title}</h2>				
				<p>The page you requested requires you to have the appropriate security clearance.</p>
				
				<c:choose><c:when test="${fn:length(_qal.list) gt 0}">
					
					<p>Please answer the following questions to prove your identity:</p>
					
					<form id="user-form" method="post" action="">
						<input type="hidden" name="success" value="${_success}" />
	
						<table>
							<c:forEach items="${_qal.list}" var="_qa" varStatus="_stat">
								<c:if test="${not empty _qa.question}">
									<tr>
										<td><label>${_qa.question}:</label></td>
										<td>
											<input type="hidden" name="question${_stat.count - 1}" value="${_qa.question}" />
											<input type="text" name="answer${_stat.count - 1}" autocomplete="off" value="" />
										</td>
									</tr>
								</c:if>						
							</c:forEach>
					</table>
								
					<button class="action" type="submit">Submit</button>					
				</form>	
				
			</c:when><c:otherwise>
				<p>Please consult your documentation for further details.</p>
			</c:otherwise></c:choose>
				
			</div>		
		</div>
	</div>
	
</geo:pageLayout>

<script>
$(function() {
	$('form#user-form').click(function() {
		$('div.in-this-section p').html('');
	})
})
</script>