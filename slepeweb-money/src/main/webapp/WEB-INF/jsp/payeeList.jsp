<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<mny:standardLayout>
	<h2>Payees <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
					
	<p><strong>Total no. of payees = ${_count}</strong></p>

	<c:choose><c:when test="${not empty _payees}">
	
		<div id="accordion">		
			<c:forEach items="${_payees}" var="_m">
				<h3>${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_p">
							<tr>
								<td class="name"><a href="${_ctxPath}/payee/form/${_p.id}">${_p.name}</a></td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:forEach>			
		</div>
					
	</c:when><c:otherwise>
		<p><strong>No payees defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

