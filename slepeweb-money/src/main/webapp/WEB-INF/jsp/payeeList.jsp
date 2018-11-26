<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Payees</h2>			
	<p><strong>Total no. of payees = ${_count}</strong></p>

	<c:choose><c:when test="${not empty _payees}">
	
		<div id="accordion">		
			<c:forEach items="${_payees}" var="_m">
				<h3>${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_p">
							<tr>
								<td data-id="${_p.id}"><i class="fas fa-bars menu-icon"></i>&nbsp;&nbsp;${_p.name}</td>
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

<mny:menuActionDialog target="/money/transaction/list/by/payee/" />
