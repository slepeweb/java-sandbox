<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/topSecret.jsp --></gen:debug>

<geo:pageLayout type="std">
		
	<div class="main standard-3col">
		<div class="leftside"><div class="in-this-section"></div></div>
		
		<div class="rightside">
			<div class="mainbody full-width">
			
				<h2 id="page-title">${_item.fields.title}</h2>				
				<div>${_item.fields.bodytext}</div>
				<br />
				
				<form id="user-form" method="post" action="">
					<input type="hidden" name="success" value="${_success}" />

					<table>
						<c:forEach items="${_qal.list}" var="_qa" varStatus="_stat">
							<c:if test="${not empty _qa.question}">
								<tr>
									<td><label>${_qa.question}:</label></td>
									<td><input name="answer${_stat.count - 1}" autocomplete="off" value="" /></td>
								</tr>
							</c:if>						
						</c:forEach>
					</table>
								
					<button class="action" type="submit">Submit</button>					
				</form>	
				
			</div>		
		</div>
	</div>
	
</geo:pageLayout>