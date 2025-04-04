<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- payeeList.jsp -->

<mny:standardLayout>

	<mny:pageHeading heading="Payees">
		<a href="add" title="Create a new payee">New payee</a>
	</mny:pageHeading>
	
	<p><strong>Total no. of payees = ${_count}</strong></p>

	<c:choose><c:when test="${not empty _payees}">
	
		<div id="accordion">		
			<c:forEach items="${_payees}" var="_m">
				<h3 title="Display payees whose names begin with ${_m.name}">${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_p">
							<tr>
								<td class="name"><a href="${_ctxPath}/payee/form/${_p.id}" 
									title="Update details of this payee">${_p.name}</a></td>
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

