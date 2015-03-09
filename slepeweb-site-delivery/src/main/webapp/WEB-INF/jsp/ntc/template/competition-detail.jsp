<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/competition-detail.jsp --></gen:debug>
	
	<script>
	$(function() {
		$.ajax("/ws/scrape/${_competition.organiserId}/${_competition.tableId}", {
			type: "POST",
			cache: false,
			data: {url: "${_competition.tableUrl}"}, 
			//dataType: "json",
			success: function(html, status, z) {
				$("#league-table").append(html);
			},
			error: function(html, status, z) {
				$("#league-table").append("Not avaliable right now");
			}
		});
	});
	</script>
	
	<div class="row uniform">
		<div class="5u 12u(3)">	
			<div class="raised-box deeper plain">
				<h2>${_competition.name}</h2>
				<table class="fixtures">
					<thead>
						<tr>
							<td>Date</td>
							<td>Fixture</td>
							<td>Result</td>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${_competition.fixtures}" var="_fixture">
							<tr>
								<td><fmt:formatDate value="${_fixture.date}" pattern="MMM dd, yyyy" /></td>
								<td>${_fixture.tie}</td>
								<td>${_fixture.scoreFor}-${_fixture.scoreAgainst}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="7u 12u(3)">	
			<div id="league-table" class="raised-box plain"></div>
		</div>
	</div>
		
	<div class="row uniform">
		<div class="12u">	
			<div class="raised-box variable-height">
				<h3>Squad</h3>
				<p>
				<c:forEach items="${_competition.squad}" var="_member" varStatus="_status"><c:if 
					test="${not _status.first}">, </c:if>${_member}</c:forEach>
				</p>
			</div>
		</div>
	</div>

</ntc:standardLayout>