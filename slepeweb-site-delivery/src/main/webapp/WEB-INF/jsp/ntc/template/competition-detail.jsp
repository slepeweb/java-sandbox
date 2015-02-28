<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/competition-detail.jsp --></gen:debug>

	<div class="row uniform">
		<div class="6u 12u(3)">	
			<h2>${_competition.name}</h2>
			<table>
				<thead>
					<tr>
						<th>Date</th>
						<th>Fixture / Result</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${_competition.fixtures}" var="_fixture">
						<tr>
							<td><fmt:formatDate value="${_fixture.date}" pattern="MMM dd, yyyy" /></td>
							<td>${_fixture.result}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="3u 0u(3)">	
		</div>
		
		<div class="3u 12u(3)">	
			<div class="raised-box">
				<h3>Squad</h3>
				<c:forEach items="${_competition.squad}" var="_member">
					${_member}<br />
				</c:forEach>
			</div>
		</div>
	</div>

</ntc:standardLayout>